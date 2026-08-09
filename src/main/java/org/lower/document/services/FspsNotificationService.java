package org.lower.document.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Через конструктор, так как ломбок не инжектирует через квалифаер
@Service
@Slf4j
public class FspsNotificationService {

/*
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    @Qualifier("virtualThreadExecutor")
    private final Executor virtualExecutor;

    public FspsNotificationService(TemplateEngine templateEngine,
                                   JavaMailSender mailSender,
                                   @Qualifier("virtualThreadExecutor")Executor virtualExecutor) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.virtualExecutor = virtualExecutor;
    }

    // Для одного документа (если используется)
    public CompletableFuture<Void> processRequestAsync(FspsNotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        String html = fillTemplate(request);
                        // --- НАЧАЛО: Измерение времени генерации PDF ---
                        long startTime = System.currentTimeMillis();
                        byte[] pdfBytes = generatePdf(html);
                        long endTime = System.currentTimeMillis();
                        log.info("PDF для должника '{}' сгенерирован за {} мс", request.getClientRequest().getFullName(), (endTime - startTime));
                        // --- КОНЕЦ: Измерение времени генерации PDF ---
                        return new GeneratedDocument("notification.pdf", pdfBytes);
                    } catch (Exception e) {
                        throw new RuntimeException("PDF generation failed", e);
                    }
                }, virtualExecutor)
                .thenApply(doc -> {
                    try {
                        byte[] zipBytes = createZip(List.of(doc));
                        sendEmailWithZip(zipBytes, "notification.zip", request.getTrustee().getMailAddress());
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException("ZIP/email failed", e);
                    }
                });
    }

    // Поддержка множества документов (на будущее)
    public CompletableFuture<Void> processMultipleRequestsAsync(List<FspsNotificationRequest> requests) {
        List<CompletableFuture<GeneratedDocument>> futures = requests.stream()
                .map(req -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String html = fillTemplate(req);
                        // --- НАЧАЛО: Измерение времени генерации PDF ---
                        long startTime = System.currentTimeMillis();
                        byte[] pdf = generatePdf(html);
                        long endTime = System.currentTimeMillis();
                        log.info("PDF для должника '{}' сгенерирован за {} мс", req.getClientRequest().getFullName(), (endTime - startTime));
                        // --- КОНЕЦ: Измерение времени генерации PDF ---
                        return new GeneratedDocument(
                                "fsps_" + req.getClientRequest().getFullName().replaceAll("\\s+", "_") + ".pdf", // имя файла с именем должника
                                pdf
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to generate PDF for request", e);
                    }
                }, virtualExecutor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    List<GeneratedDocument> docs;
                    try {
                        docs = futures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                    } catch (Exception e) {
                        throw new RuntimeException("One or more PDFs failed", e);
                    }

                    try {
                        long startTime = System.currentTimeMillis();
                        byte[] zipBytes = createZip(docs);
                        long endTime = System.currentTimeMillis();
                        log.info("ZIP архив с {} файлами создан за {} мс", docs.size(), (endTime - startTime));

                        String toEmail = requests.isEmpty() ? "igarik10@yandex.ru" : requests.getFirst().getTrustee().getMailAddress();
                        sendEmailWithZip(zipBytes, "documents.zip", toEmail);
                    } catch (Exception e) {
                        throw new RuntimeException("ZIP/email failed", e);
                    }
                });
    }

    private String fillTemplate(FspsNotificationRequest request) {
        Context ctx = new Context();
        ctx.setVariable("courtDecision", request.getCourtDecision());
        ctx.setVariable("debtor", request.getClientRequest());
        ctx.setVariable("trustee", request.getTrustee());
        ctx.setVariable("procedure", request.getProcedure());
        ctx.setVariable("recipientAddress", request.getRecipientAddress());
        ctx.setVariable("attachments", request.getAttachments());
        return templateEngine.process("fsps-notification", ctx);
    }

    private byte[] generatePdf(String htmlContent) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            ConverterProperties props = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, pdfDoc, props);
        }
        return baos.toByteArray();
    }

    private byte[] createZip(List<GeneratedDocument> documents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (GeneratedDocument doc : documents) {
                ZipEntry entry = new ZipEntry(doc.filename);
                zos.putNextEntry(entry);
                zos.write(doc.content);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    private void sendEmailWithZip(byte[] zipBytes, String zipFileName, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Запрос-уведомление ФССП");
            helper.setText("Во вложении файл(ы) запроса-уведомления.");

            ByteArrayResource resource = new ByteArrayResource(zipBytes);
            helper.addAttachment(zipFileName, resource);

            mailSender.send(message);
            log.info("ZIP с {} документами отправлен на {}", message.getSize(), toEmail);
        } catch (MessagingException e) {
            log.error("Ошибка при отправке email", e);
            throw new RuntimeException("Не удалось отправить email", e);
        }
    }

    @Value
    static class GeneratedDocument {
        String filename;
        byte[] content;
    }*/
}