package org.lower.document.services;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dao.ClientDao;
import org.lower.document.dao.OwnerDao;
import org.lower.document.dto.BatchGenerationRequest;
import org.lower.document.dto.GeneratedFileDto;
import org.lower.document.dto.RecipientInfoDto;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    private final TemplateEngine templateEngine;
    private final ClientDao clientService;
    private final OwnerDao lawyerService;
    // Возможно, понадобится сервис для получения адресов органов по региону
    private final RecipientLookupService recipientLookupService;

    /**
     * Основной метод генерации пакета документов по ID.
     */
    public List<GeneratedFileDto> generateDocuments(BatchGenerationRequest request) {
        List<GeneratedFileDto> result = new ArrayList<>();

        // 1. Загружаем данные из БД
        ClientsRecord client = clientService.getClientByUuid(request.getClientId());
        OwnersRecord lawyer = lawyerService.getLawyerByUuid(request.getLawyerId());

        // 2. Проходим по каждому запрошенному типу документа
        for (String docType : request.getDocumentTypes()) {
            try {
                // Определяем данные получателя (органа) для этого типа документа
                // Логика может быть разной: хардкод, справочник в БД, зависимость от региона клиента
                var recipient = recipientLookupService.getRecipient(docType, client);

                // 3. Формируем контекст для Thymeleaf
                Context context = buildContext(client, lawyer, recipient, request.getRequestDate(), docType);

                // 4. Рендерим HTML
                String templateName = getTemplateName(docType);
                String html = templateEngine.process(templateName, context);

                // 5. Конвертируем в PDF
                byte[] pdfBytes = convertHtmlToPdf(html);

                // 6. Формируем имя файла
                String fileName = generateFileName(docType, client.getFullName(), request.getRequestDate());

                result.add(new GeneratedFileDto(fileName, Base64.getEncoder().encodeToString(pdfBytes)));

                log.info("Сгенерирован документ: {} для клиента: {}", docType, client.getId());

            } catch (Exception e) {
                log.error("Ошибка генерации документа {}: {}", docType, e.getMessage());
                throw new RuntimeException("Ошибка при генерации " + docType + ": " + e.getMessage(), e);
            }
        }

        return result;
    }

    /**
     * Сборка контекста переменных для шаблона.
     */
    private Context buildContext(ClientsRecord client, OwnersRecord lawyer,
                                 RecipientInfoDto recipient,
                                 java.time.LocalDate requestDate, String docType) {

        Context context = new Context();

        // --- Данные должника (маппинг из Record в объект или прямо поля) ---
        // Лучше создать DTO DebtorInfo из ClientsRecord, но для краткости покажем концепцию
        context.setVariable("debtor", mapClientToDebtorInfo(client));

        // --- Данные суда (предполагаем, что они хранятся в таблице clients или связаны) ---
        // Если суд общий для всех запросов клиента, берем из клиента.
        // Если нет - нужна отдельная таблица court_cases.
        context.setVariable("courtDecision", mapClientToCourtInfo(client));

        // --- Процедура (обычно фиксирована или хранится в деле о банкротстве) ---
        context.setVariable("procedure", "реализации имущества");

        // --- Данные юриста (Владельца) ---
        context.setVariable("lawyer", mapOwnerToLawyerInfo(lawyer));

        // --- Данные получателя (Органа) ---
        context.setVariable("recipientOrgName", recipient.getOrgName());
        context.setVariable("recipientOrgAddress", recipient.getOrgAddress());
        context.setVariable("recipientOrgNote", recipient.getOrgNote());

        // --- Дата запроса (передается с фронта) ---
        context.setVariable("requestDate", requestDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        // --- Тип шаблона (для условной логики внутри шаблона, если нужно) ---
        context.setVariable("templateType", docType);

        return context;
    }

    // --- Вспомогательные методы маппинга (примерные реализации) ---

    private DebtorInfo mapClientToDebtorInfo(ClientsRecord client) {
        var info = new DebtorInfo();
        info.setFullName(client.getFullName());
        // Разбиваем ФИО на короткие для подписи, если в БД нет отдельного поля
        String[] parts = client.getFullName().split(" ");
        if (parts.length >= 2) {
            info.setFullNameShort(parts[0] + " " + parts[1].charAt(0) + ". " + (parts.length > 2 ? parts[2].charAt(0) + "." : ""));
        } else {
            info.setFullNameShort(client.getFullName());
        }

        info.setBirthDate(client.getBirthDate());
        info.setBirthPlace(client.getBirthPlace());
        info.setInn(client.getInn());
        info.setSnils(client.getSnils());
        info.setAddress(client.getAddress());
        return info;
    }

    private CourtInfo mapClientToCourtInfo(ClientsRecord client) {
        var info = new CourtInfo();
        // Предполагаем, что в таблице clients есть поля суда.
        // Если нет, нужно джойнить таблицу court_cases по client_id
        info.setCourtName(client.getCourtName());       // Примерное поле
        info.setDecisionDate(client.getCourtDate());    // Примерное поле
        info.setCaseNumber(client.getCaseNumber());     // Примерное поле
        return info;
    }

    private LawyerInfo mapOwnerToLawyerInfo(OwnersRecord owner) {
        var info = new LawyerInfo();
        info.setFullName(owner.getFullName());
        info.setFullNameShort(owner.getFullNameShort());
        info.setMailAddress(owner.getMailAddress());
        info.setEmail(owner.getEmail());
        info.setInn(owner.getUserInn());
        info.setSnils(owner.getUserSnils());
        info.setSroName(owner.getSroName());
        info.setSroOgrn(owner.getSroOgrn());
        info.setSroInn(owner.getSroInn());
        info.setSroAddress(owner.getSroAddress());
        return info;
    }

    private byte[] convertHtmlToPdf(String html) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, pdfDocument, properties);
        }
        return baos.toByteArray();
    }

    private String generateFileName(String type, String debtorFullName, java.time.LocalDate date) {
        String surname = debtorFullName.split(" ")[0];
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s_%s.pdf", type.toUpperCase(), surname, dateStr, UUID.randomUUID().toString().substring(0, 8));
    }

    private String getTemplateName(String type) {
        return switch (type.toLowerCase()) {
            case "fsps" -> "fsps-request";
            case "osfr" -> "osfr-request";
            case "bti" -> "bti-request";
            case "mifns" -> "mifns-request";
            case "mchs" -> "mchs-request";
            case "gibdd" -> "gibdd-request";
            case "rosimushchestvo" -> "rosimushchestvo-request";
            case "rospatent" -> "rospatent-request";
            case "rosaviatsia" -> "rosaviatsia-request";
            case "rostekhnadzor" -> "rostekhnadzor-request";
            case "court" -> "court-request";
            case "rosgruard" -> "rosgruard-request";
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        };
    }
}