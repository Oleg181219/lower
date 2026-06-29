package org.lower.document.services.document;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.lower.document.dto.FsspDocumentData;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public abstract class AbstractPdfBoxDocumentGenerator implements PdfDocumentGenerator {

    private static final String FONT_REGULAR_PATH = "/fonts/PTSerif-Regular.ttf";
    private static final String FONT_BOLD_PATH = "/fonts/PTSerif-Bold.ttf";

    protected static final float FONT_SIZE = 11f;
    protected static final float LINE_HEIGHT = 15f;
    protected static final float LEFT_MARGIN = 70f;
    protected static final float RIGHT_MARGIN = 70f;
    protected static final float TOP_MARGIN = 60f;
    protected static final float BOTTOM_MARGIN = 50f;
    protected static final float PAGE_WIDTH = 595.28f - LEFT_MARGIN - RIGHT_MARGIN;
    private static final float PAGE_HEIGHT = 841.89f;

    @Override
    public byte[] generate(FsspDocumentData data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont regular = loadFont(document, FONT_REGULAR_PATH);
            PDFont bold = loadFont(document, FONT_BOLD_PATH);

            PDPageContentStream[] csHolder = new PDPageContentStream[1];
            csHolder[0] = new PDPageContentStream(document, page);

            float[] yHolder = new float[1];
            yHolder[0] = PAGE_HEIGHT - TOP_MARGIN;

            try {
                drawHeader(csHolder, document, regular, bold, data, yHolder);
                drawDocumentTitle(csHolder, document, bold, yHolder);
                drawBody(csHolder, document, regular, data, yHolder);
                drawAppendix(csHolder, document, regular, bold, data, yHolder);
                drawSignatures(csHolder, document, regular, bold, data, yHolder);
            } finally {
                if (csHolder[0] != null) {
                    csHolder[0].close();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public abstract String getDocType();

    protected abstract void drawDocumentTitle(PDPageContentStream[] cs, PDDocument doc, PDFont bold, float[] y) throws IOException;
    protected abstract void drawBody(PDPageContentStream[] cs, PDDocument doc, PDFont regular, FsspDocumentData data, float[] y) throws IOException;

    protected void drawHeader(PDPageContentStream[] cs, PDDocument doc, PDFont regular, PDFont bold, FsspDocumentData data, float[] y) throws IOException {
        writeCenteredText(cs, doc, bold, "Финансовый управляющий", y);
        writeCenteredText(cs, doc, bold, data.trustee().fullName(), y);
        writeCenteredText(cs, doc, regular, "адрес для направления корреспонденции: " + data.trustee().mailAddress(), y);
        writeCenteredText(cs, doc, regular, "адрес электронной почты: " + data.trustee().email(), y);
        y[0] -= 10;

        cs[0].moveTo(LEFT_MARGIN, y[0]);
        cs[0].lineTo(595.28f - RIGHT_MARGIN, y[0]);
        cs[0].stroke();
        y[0] -= 15;

        writeRightText(cs, doc, bold, data.recipient().orgName(), y);
        writeRightText(cs, doc, regular, data.recipient().orgAddress(), y);

        if (data.recipient().orgNote() != null && !data.recipient().orgNote().isBlank()) {
            writeRightText(cs, doc, regular, "(" + data.recipient().orgNote() + ")", y);
        }
        y[0] -= 20;
    }

    protected void drawAppendix(PDPageContentStream[] cs, PDDocument doc, PDFont regular, PDFont bold, FsspDocumentData data, float[] y) throws IOException {
        y[0] -= 5;
        writeText(cs, doc, bold, "Приложение:", y);
        writeText(cs, doc, regular, "1. Копия Решения " + data.courtDecision().courtName() + " " +
                data.courtDecision().decisionDate() + " по делу №" + data.courtDecision().caseNumber(), y);
        writeText(cs, doc, regular, "2. Копия паспорта должника;", y);
        writeText(cs, doc, regular, "3. Копия паспорта финансового управляющего", y);
        y[0] -= 20;
    }

    protected void drawSignatures(PDPageContentStream[] cs, PDDocument doc, PDFont regular, PDFont bold, FsspDocumentData data, float[] y) throws IOException {
        writeText(cs, doc, regular, "Финансовый управляющий", y);
        writeText(cs, doc, bold, data.debtor().fullNameShortGenitive(), y);

        String trusteeShort = data.trustee().fullNameShort();
        float trusteeWidth = bold.getStringWidth(trusteeShort) * FONT_SIZE / 1000;
        cs[0].beginText();
        cs[0].setFont(bold, FONT_SIZE);
        cs[0].newLineAtOffset(595.28f - RIGHT_MARGIN - trusteeWidth, y[0] + LINE_HEIGHT);
        cs[0].showText(trusteeShort);
        cs[0].endText();
    }

    private PDFont loadFont(PDDocument document, String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IOException("Шрифт не найден: " + path);
        }
        try (InputStream is = resource.getInputStream()) {
            return PDType0Font.load(document, is);
        }
    }

    private void newPage(PDPageContentStream[] csHolder, PDDocument document, float[] yHolder) throws IOException {
        csHolder[0].close();
        PDPage newPage = new PDPage(PDRectangle.A4);
        document.addPage(newPage);
        csHolder[0] = new PDPageContentStream(document, newPage);
        yHolder[0] = PAGE_HEIGHT - TOP_MARGIN;
    }

    protected void writeText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        text = sanitizeText(text);
        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(LEFT_MARGIN, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    protected void writeCenteredText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) * FONT_SIZE / 1000;
        float x = (595.28f - textWidth) / 2;

        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(x, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    protected void writeRightText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) * FONT_SIZE / 1000;
        float x = 595.28f - RIGHT_MARGIN - textWidth;

        if (x < LEFT_MARGIN) x = LEFT_MARGIN;

        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(x, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    protected void writeParagraph(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder, boolean indent) throws IOException {
        if (text == null) return;
        text = sanitizeText(text).trim();

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        float indentSize = indent ? 35f : 0;

        for (String word : words) {
            String testLine = !line.isEmpty() ? line + " " + word : word;
            float lineWidth = font.getStringWidth(testLine) * FONT_SIZE / 1000;

            if (lineWidth > PAGE_WIDTH && !line.isEmpty()) {
                if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
                    newPage(csHolder, document, yHolder);
                }

                csHolder[0].beginText();
                csHolder[0].setFont(font, FONT_SIZE);
                csHolder[0].newLineAtOffset(LEFT_MARGIN + indentSize, yHolder[0]);
                csHolder[0].showText(line.toString());
                csHolder[0].endText();

                line = new StringBuilder(word);
                yHolder[0] -= LINE_HEIGHT;
                indentSize = 0;
            } else {
                if (!line.isEmpty()) line.append(" ");
                line.append(word);
            }
        }

        if (!line.isEmpty()) {
            if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
                newPage(csHolder, document, yHolder);
            }

            csHolder[0].beginText();
            csHolder[0].setFont(font, FONT_SIZE);
            csHolder[0].newLineAtOffset(LEFT_MARGIN + indentSize, yHolder[0]);
            csHolder[0].showText(line.toString());
            csHolder[0].endText();
            yHolder[0] -= LINE_HEIGHT;
        }
    }

    private String sanitizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\n\\r\\t]+", " ").trim();
    }
}