package org.lower.document.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.lower.document.services.clientpdf.document.PdfClientDocumentGenerator;
import org.lower.document.util.TimeProvider;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfClientGenerationService {

    private final List<PdfClientDocumentGenerator> generators;
    private final TimeProvider timeProvider;

    private ExecutorService pdfExecutor;

    @PostConstruct
    public void init() {
        this.pdfExecutor = Executors.newFixedThreadPool(
                4,
                Thread.ofPlatform().name("pdf-gen-", 0).factory()
        );
        log.info("PdfClientGenerationService initialized with {} generators", generators.size());
    }

    @PreDestroy
    public void shutdown() {
        if (pdfExecutor != null) {
            pdfExecutor.shutdown();
            try {
                if (!pdfExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    pdfExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                pdfExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("PdfClientGenerationService executor shut down");
        }
    }

    /**
     * Генерирует все 12 типов документов и упаковывает их в ZIP-архив.
     * Стримит архив напрямую в OutputStream (без сохранения в память или БД).
     */
    public void generateAndStreamToZip(ClientDocumentRequest request, OutputStream outputStream) {
        log.info("Начинаем генерацию {} документов для клиента {}",
                generators.size(), request.lastName());

        // Параллельная генерация всех PDF
        List<GeneratedPdf> generatedPdfs = generators.parallelStream()
                .map(generator -> generateSinglePdf(generator, request))
                .filter(Objects::nonNull)
                .toList();

        // Упаковка в ZIP
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

    private GeneratedPdf generateSinglePdf(PdfClientDocumentGenerator generator,
                                           ClientDocumentRequest request) {
        try {
            byte[] pdfBytes = generator.generate(request);
            String fileName = generateFileName(generator.getDocType(), request.lastName());
            log.debug("Сгенерирован документ типа {}: {}", generator.getDocType(), fileName);
            return new GeneratedPdf(fileName, pdfBytes);
        } catch (Exception e) {
            log.error("Ошибка при генерации документа типа {}: {}",
                    generator.getDocType(), e.getMessage(), e);
            return null; // Пропускаем этот документ, остальные продолжаем генерировать
        }
    }

    private String generateFileName(String docType, String lastName) {
        String dateStr = timeProvider.today().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s.pdf",
                docType.toUpperCase(),
                lastName,
                dateStr);
    }

    private record GeneratedPdf(String fileName, byte[] pdfBytes) {
    }
}