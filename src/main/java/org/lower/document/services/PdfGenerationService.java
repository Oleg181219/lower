package org.lower.document.services;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dao.CourtDecisionDao;
import org.lower.document.dao.OrgDao;
import org.lower.document.dto.*;
import org.lower.document.dto.request.BatchGenerationRequest;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.lower.document.jooq.codegen.tables.records.CourtDecisionsRecord;
import org.lower.document.jooq.codegen.tables.records.CourtOrgsRecord;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.lower.document.services.document.PdfDocumentGenerator;
import org.lower.document.util.MoscowTimeProvider;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    private final UtilService utilService;
    private final OrgDao orgDao;
    private final CourtDecisionDao courtDecisionDao;
    private final ClientService clientService;
    private final MoscowTimeProvider timeProvider;
    private final PdfDocumentGenerator pdfGenerator;

    // Пул потоков для параллельной генерации PDF (CPU-bound задача)
    // Ограничиваем до 4 потоков, чтобы не перегружать CPU и память
    private final ExecutorService pdfExecutor = Executors.newFixedThreadPool(
            4,
            Thread.ofPlatform().name("pdf-gen-", 0).factory()
    );

    /**
     * Основной метод: генерирует документы и стримит их в ZIP напрямую в OutputStream.
     * Не использует промежуточное хранение в Base64.
     */
    public void generateAndStreamToZip(BatchGenerationRequest request, OutputStream outputStream) {
        log.info("Начинаем генерацию {} документов для клиента {}",
                request.getDocumentsIds().size(), request.getClientId());

        // 1. Загружаем общие данные из БД (один раз на все документы)
        OwnersRecord currentOwner = Optional.ofNullable(utilService.getOwnersRecord())
                .orElseThrow(() -> new RuntimeException("Current user is not an owner"));

        ClientsRecord client = Optional.ofNullable(clientService.getClientById(request.getClientId()))
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Загружаем все активные организации для региона клиента
        List<CourtOrgsRecord> orgs = orgDao.getActiveOrganizationsRecByRegion(client.getRegion());

        // Загружаем дело, по которому идет формирование документов.
        CourtDecisionsRecord courtDecisionsRecord =
                Optional.ofNullable(courtDecisionDao.findByClientIdAnOwnerId(client.getId(), currentOwner.getId()))
                        .orElseThrow(() -> new RuntimeException("Court decision not found"));
        // Создаем мапу для быстрого поиска организации по ID записи
        // Ключ: id (UUID из таблицы court_orgs), Значение: CourtOrgsRecord
        Map<UUID, CourtOrgsRecord> orgsById = orgs.stream()
                .collect(Collectors.toMap(
                        CourtOrgsRecord::getId,
                        Function.identity(),
                        (existing, replacement) -> {
                            log.warn("Дублирующийся ID организации: {}. Используется первая запись.", existing.getId());
                            return existing;
                        }
                ));

        // 2. Параллельная генерация PDF для каждого документа
        // documentsIds — это список UUID записей из таблицы court_orgs
        List<GeneratedPdf> generatedPdfs = request.getDocumentsIds().parallelStream()
                .map(orgId -> generateSinglePdf(orgId, client, currentOwner, orgsById, courtDecisionsRecord))
                .toList();

        // 3. Последовательная запись в ZIP (ZipOutputStream не потокобезопасен)
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for (GeneratedPdf pdf : generatedPdfs) {
                ZipEntry entry = new ZipEntry(pdf.fileName());
                zipOut.putNextEntry(entry);
                zipOut.write(pdf.pdfBytes());
                zipOut.closeEntry();
                log.debug("Добавлен в ZIP: {}", pdf.fileName());
            }
            zipOut.finish();
            log.info("ZIP-архив успешно сформирован, всего документов: {}", generatedPdfs.size());
        } catch (Exception e) {
            log.error("Ошибка при формировании ZIP-архива", e);
            throw new RuntimeException("Не удалось создать ZIP-архив", e);
        }
    }

    /**
     * Генерация одного PDF документа (вызывается параллельно для каждого orgId)
     *
     * @param orgId UUID записи из таблицы court_orgs
     */
    @SneakyThrows
    private GeneratedPdf generateSinglePdf(UUID orgId, ClientsRecord client, OwnersRecord owner,
                                           Map<UUID, CourtOrgsRecord> orgsById, CourtDecisionsRecord courtDecisionsRecord) {
        CourtOrgsRecord orgRecord = orgsById.get(orgId);
        String docType = orgRecord.getDocType();

        // Создаем DTO с данными
        FsspDocumentData data = buildFsspDocumentData(client, owner, orgRecord, courtDecisionsRecord);

        // Генерируем PDF через интерфейс
        byte[] pdfBytes = pdfGenerator.generate(data);

        String fileName = generateFileName(docType, client.getFullName());
        return new GeneratedPdf(fileName, pdfBytes);
    }

    private FsspDocumentData buildFsspDocumentData(ClientsRecord client, OwnersRecord owner,
                                                   CourtOrgsRecord org, CourtDecisionsRecord courtDecisionsRecord) {
        return new FsspDocumentData(
                new TrusteeData(
                        owner.getFullName(),
                        owner.getFullNameShort(),
                        client.getFullNameGenitive(),
                        owner.getMailAddress(),
                        owner.getEmail(),
                        owner.getUserInn(),
                        owner.getUserSnils(),
                        owner.getSroName(),
                        owner.getSroOgrn(),
                        owner.getSroInn(),
                        owner.getSroAddress()
                ),
                new RecipientData(org.getOrgName(), org.getOrgAddress()),
                new DebtorData(
                        client.getFullName(),
                        client.getFullNameGenitive(),
                        client.getFullNameShort(),
                        client.getFullNameShortGenitive(),
                        client.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        client.getBirthPlace(),
                        client.getInn(),
                        client.getSnils(),
                        client.getAddress()
                ),
                new CourtDecisionData(
                        courtDecisionsRecord.getCourtName(),
                        courtDecisionsRecord.getDecisionDate() != null ?
                                courtDecisionsRecord.getDecisionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "01.12.2026",
                        courtDecisionsRecord.getCaseNumber()
                ),
                "реализации имущества"
        );
    }

    /**
     * Генерация имени файла для PDF
     */
    private String generateFileName(String type, String debtorFullName) {
        String surname = debtorFullName.split(" ")[0];
        String dateStr = timeProvider.today().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s.pdf",
                type.toUpperCase(),
                surname,
                dateStr);
    }

    /**
     * Получение имени шаблона по типу документа.
     * Реализован только FSSP, остальные - заглушки.
     */
    private String getTemplateName(String type) {
        return switch (type.toLowerCase()) {
            case "fsps" -> "fsps-notification";
            case "osfr" -> throw new UnsupportedOperationException("Шаблон OSFR еще не реализован");
            case "bti" -> throw new UnsupportedOperationException("Шаблон BTI еще не реализован");
            case "mifns" -> throw new UnsupportedOperationException("Шаблон MIFNS еще не реализован");
            case "mchs" -> throw new UnsupportedOperationException("Шаблон MCHS еще не реализован");
            case "gibdd" -> throw new UnsupportedOperationException("Шаблон GIBDD еще не реализован");
            case "rosimushchestvo" ->
                    throw new UnsupportedOperationException("Шаблон ROSIMUSHCHESTVO еще не реализован");
            case "rospatent" -> throw new UnsupportedOperationException("Шаблон ROSPATENT еще не реализован");
            case "rosaviatsia" -> throw new UnsupportedOperationException("Шаблон ROSAVIATSIA еще не реализован");
            case "rostekhnadzor" -> throw new UnsupportedOperationException("Шаблон ROSTEKHNADZOR еще не реализован");
            case "court" -> throw new UnsupportedOperationException("Шаблон COURT еще не реализован");
            case "rosgruard" -> throw new UnsupportedOperationException("Шаблон ROSGRUARD еще не реализован");
            default -> throw new IllegalArgumentException("Неизвестный тип документа: " + type);
        };
    }

    /**
     * Внутренний record для хранения результата генерации одного PDF
     */
    private record GeneratedPdf(String fileName, byte[] pdfBytes) {
    }
}