package org.lower.document.services;

import jakarta.annotation.PostConstruct;
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
import org.lower.document.util.TimeProvider;
import org.springframework.stereotype.Service;

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
    private final TimeProvider timeProvider;
    private final List<PdfDocumentGenerator> generators;

    private Map<String, PdfDocumentGenerator> generatorsByType;
    private ExecutorService pdfExecutor;

    @PostConstruct
    public void init() {
        this.generatorsByType = generators.stream()
                .collect(Collectors.toMap(
                        PdfDocumentGenerator::getDocType,
                        Function.identity(),
                        (existing, replacement) -> {
                            log.warn("Дублирующийся генератор типа: {}. Используется первый.", existing.getDocType());
                            return existing;
                        }
                ));

        this.pdfExecutor = Executors.newFixedThreadPool(
                4,
                Thread.ofPlatform().name("pdf-gen-", 0).factory()
        );
    }

    public void generateAndStreamToZip(BatchGenerationRequest request, OutputStream outputStream) {
        log.info("Начинаем генерацию {} документов для клиента {}",
                request.getDocumentsIds().size(), request.getClientId());

        OwnersRecord currentOwner = Optional.ofNullable(utilService.getOwnersRecord())
                .orElseThrow(() -> new RuntimeException("Current user is not an owner"));

        ClientsRecord client = Optional.ofNullable(clientService.getClientById(request.getClientId()))
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<CourtOrgsRecord> orgs = orgDao.getActiveOrganizationsRecByRegion(client.getRegion());

        CourtDecisionsRecord courtDecisionsRecord =
                Optional.ofNullable(courtDecisionDao.findByClientIdAnOwnerId(client.getId(), currentOwner.getId()))
                        .orElseThrow(() -> new RuntimeException("Court decision not found"));

        Map<UUID, CourtOrgsRecord> orgsById = orgs.stream()
                .collect(Collectors.toMap(
                        CourtOrgsRecord::getId,
                        Function.identity(),
                        (existing, replacement) -> {
                            log.warn("Дублирующийся ID организации: {}. Используется первая запись.", existing.getId());
                            return existing;
                        }
                ));

        List<GeneratedPdf> generatedPdfs = request.getDocumentsIds().parallelStream()
                .map(orgId -> generateSinglePdf(orgId, client, currentOwner, orgsById, courtDecisionsRecord))
                .toList();

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

    @SneakyThrows
    private GeneratedPdf generateSinglePdf(UUID orgId, ClientsRecord client, OwnersRecord owner,
                                           Map<UUID, CourtOrgsRecord> orgsById, CourtDecisionsRecord courtDecisionsRecord) {
        CourtOrgsRecord orgRecord = orgsById.get(orgId);
        if (orgRecord == null) {
            throw new IllegalStateException("Организация с ID " + orgId + " не найдена");
        }

        String docType = orgRecord.getDocType();
        PdfDocumentGenerator generator = generatorsByType.get(docType);

        if (generator == null) {
            throw new IllegalStateException("Генератор для типа документа " + docType + " не найден");
        }

        FsspDocumentData data = buildFsspDocumentData(client, owner, orgRecord, courtDecisionsRecord);
        byte[] pdfBytes = generator.generate(data);

        String fileName = generateFileName(docType, client.getFullName());
        return new GeneratedPdf(fileName, pdfBytes);
    }

    private FsspDocumentData buildFsspDocumentData(ClientsRecord client, OwnersRecord owner,
                                                   CourtOrgsRecord org, CourtDecisionsRecord courtDecisionsRecord) {
        return new FsspDocumentData(
                new TrusteeData(
                        owner.getFullName(),
                        owner.getFullNameShort(),
                        owner.getFullNameGenitive(),
                        owner.getMailAddress(),
                        owner.getEmail(),
                        owner.getUserInn(),
                        owner.getUserSnils(),
                        owner.getSroName(),
                        owner.getSroOgrn(),
                        owner.getSroInn(),
                        owner.getSroAddress()
                ),
                new RecipientData(
                        org.getOrgName(),
                        org.getOrgAddress(),
                        org.getOrgNote()
                ),
                new DebtorData(
                        client.getFullName(),
                        client.getFullNameGenitive(),
                        client.getFullNameShort(),
                        client.getFullNameShortGenitive(),
                        "client.getFullNameInstrumental()",
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

    private String generateFileName(String type, String debtorFullName) {
        String surname = debtorFullName.split("\\s+")[0];
        String dateStr = timeProvider.today().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s.pdf",
                type.toUpperCase(),
                surname,
                dateStr);
    }

    private record GeneratedPdf(String fileName, byte[] pdfBytes) {
    }
}