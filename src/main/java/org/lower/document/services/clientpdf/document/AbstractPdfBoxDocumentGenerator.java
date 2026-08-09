package org.lower.document.services.clientpdf.document;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.lower.document.dto.request.ClientDocumentRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractPdfBoxDocumentGenerator implements PdfClientDocumentGenerator {

    private static final String FONT_REGULAR_PATH = "fonts/PTSerif-Regular.ttf";
    private static final String FONT_BOLD_PATH = "fonts/PTSerif-Bold.ttf";

    protected static final float FONT_SIZE = 11f;
    protected static final float LINE_HEIGHT = 15f;
    protected static final float LEFT_MARGIN = 70f;
    protected static final float RIGHT_MARGIN = 70f;
    protected static final float TOP_MARGIN = 60f;
    protected static final float BOTTOM_MARGIN = 50f;
    protected static final float PAGE_WIDTH = 595.28f - LEFT_MARGIN - RIGHT_MARGIN;
    private static final float PAGE_HEIGHT = 841.89f;

    protected static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ==================== ХАРДКОД: финансовый управляющий ====================
    protected static final String TRUSTEE_FULL_NAME = "Степаньянц Светлана Анатольевна";
    protected static final String TRUSTEE_FULL_NAME_GENITIVE = "Степаньянц Светланы Анатольевны";
    protected static final String TRUSTEE_SHORT_NAME = "Степаньянц С.А.";
    protected static final String TRUSTEE_INN = "616511012560";
    protected static final String TRUSTEE_SNILS = "14063088738";
    protected static final String TRUSTEE_MAIL_ADDRESS = "344000, г.Ростов-на-Дону, пер.Халтуринский, 4, оф. 6";
    protected static final String TRUSTEE_EMAIL = "svetlanaot@yandex.ru";

    // ==================== ХАРДКОД: СРО ====================
    protected static final String SRO_NAME = "СРО АУ \"Лига\"";
    protected static final String SRO_OGRN = "1045803007326";
    protected static final String SRO_INN = "5836140708";
    protected static final String SRO_ADDRESS = "г. Пенза, ул. Володарского, 9";

    // ==================== ХАРДКОД: суд и процедура ====================
    protected static final String COURT_NAME_GENITIVE = "Арбитражного суда Ростовской области";
    protected static final String PROCEDURE = "реализации имущества";

    // ==================== ХАРДКОД: фолбэки дат из шаблонов ====================
    private static final String FALLBACK_THREE_YEARS_DATE = "17.09.2019";
    private static final int FALLBACK_THREE_YEARS_YEAR = 2019;

    // =====================================================================
    //                            ГЕНЕРАЦИЯ
    // =====================================================================

    @Override
    public byte[] generate(ClientDocumentRequest data) throws IOException {
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
                drawHeader(csHolder, document, regular, bold, yHolder);
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

    // =====================================================================
    //                 ТО, ЧТО ЗАДАЁТ КОНКРЕТНЫЙ ГЕНЕРАТОР
    // =====================================================================

    public abstract String getDocType();

    /** Название организации-получателя */
    protected abstract String getRecipientOrgName();

    /** Адрес организации-получателя */
    protected abstract String getRecipientOrgAddress();

    /** Сноска под адресом получателя; null — если сноски нет */
    protected abstract String getRecipientOrgNote();

    /** Абзац(ы) с самим запросом — единственное реальное отличие документов */
    protected abstract void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc,
                                                 PDFont regular, ClientDocumentRequest data,
                                                 float[] y) throws IOException;

    // =====================================================================
    //                            ХУКИ (опционально)
    // =====================================================================

    protected String getDocumentTitle() {
        return "ЗАПРОС";
    }

    /** true — добавить в финальный абзац «, по адресу электронной почты: ...» (только ОСФР) */
    protected boolean appendTrusteeEmailInFooter() {
        return false;
    }

    /** Юридическое обоснование. По умолчанию 3 стандартных абзаца; ФССП добавляет свои. */
    protected void drawLegalBasisParagraphs(PDPageContentStream[] cs, PDDocument doc,
                                            PDFont regular, ClientDocumentRequest data,
                                            float[] y) throws IOException {
        drawStandardLegalParagraphs(cs, doc, regular, y);
    }

    // =====================================================================
    //                        ШАБЛОННЫЙ МЕТОД ТЕЛА
    // =====================================================================

    protected final void drawBody(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                  ClientDocumentRequest data, float[] y) throws IOException {
        drawCourtDecisionParagraph(cs, doc, regular, data, y);
        drawTrusteeParagraph(cs, doc, regular, y);
        drawLegalBasisParagraphs(cs, doc, regular, data, y);
        drawRequestParagraph(cs, doc, regular, data, y);
        drawFooterParagraphs(cs, doc, regular, y);
    }

    // =====================================================================
    //                        ГОТОВЫЕ БЛОКИ (final)
    // =====================================================================

    protected final void drawHeader(PDPageContentStream[] cs, PDDocument doc,
                                    PDFont regular, PDFont bold, float[] y) throws IOException {
        writeCenteredText(cs, doc, bold, "Финансовый управляющий " + TRUSTEE_FULL_NAME, y);
        writeCenteredText(cs, doc, regular, "адрес для направления корреспонденции: " + TRUSTEE_MAIL_ADDRESS, y);
        writeCenteredText(cs, doc, regular, "адрес электронной почты: " + TRUSTEE_EMAIL, y);
        y[0] -= 10;

        cs[0].moveTo(LEFT_MARGIN, y[0]);
        cs[0].lineTo(595.28f - RIGHT_MARGIN, y[0]);
        cs[0].stroke();
        y[0] -= 15;

        writeRightText(cs, doc, bold, getRecipientOrgName(), y);
        writeRightText(cs, doc, regular, getRecipientOrgAddress(), y);
        String note = getRecipientOrgNote();
        if (note != null && !note.isBlank()) {
            writeRightText(cs, doc, regular, "(" + note + ")", y);
        }
        y[0] -= 20;
    }

    protected final void drawDocumentTitle(PDPageContentStream[] cs, PDDocument doc,
                                           PDFont bold, float[] y) throws IOException {
        writeCenteredText(cs, doc, bold, getDocumentTitle(), y);
        y[0] -= 15;
    }

    protected final void drawCourtDecisionParagraph(PDPageContentStream[] cs, PDDocument doc,
                                                    PDFont regular, ClientDocumentRequest data,
                                                    float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "Решением " + COURT_NAME_GENITIVE + " от " + formatDate(data.caseDate()) +
                        " по делу №" + data.caseNumber() +
                        " в отношении гражданина " + debtorInfo(data, data.genitive()) +
                        " введена процедура " + PROCEDURE + ".", y, true);
    }

    protected final void drawTrusteeParagraph(PDPageContentStream[] cs, PDDocument doc,
                                              PDFont regular, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "Финансовым управляющим утвержден(а) " + TRUSTEE_FULL_NAME +
                        " (ИНН " + TRUSTEE_INN + ", СНИЛС " + TRUSTEE_SNILS +
                        ") - " + SRO_NAME + " (ОГРН " + SRO_OGRN +
                        ", ИНН " + SRO_INN + ", " + SRO_ADDRESS + ").", y, true);
    }

    /** Три стандартных юридических абзаца (20.3 абз.7, 20.3 абз.10, 213.9 п.7) */
    protected final void drawStandardLegalParagraphs(PDPageContentStream[] cs, PDDocument doc,
                                                     PDFont regular, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В соответствии с абз. 7 п. 1 ст. 20.3 ФЗ «О несостоятельности (банкротстве)» " +
                        "арбитражному управляющему представлено право запрашивать необходимые сведения о должнике, " +
                        "о лицах, входящих в состав органов управления должника, о контролирующих лицах, о " +
                        "принадлежащем им имуществе (в том числе имущественных правах), о контрагентах и об " +
                        "обязательствах должника у физических лиц, юридических лиц, государственных органов, " +
                        "органов управления государственными внебюджетными фондами Российской Федерации и органов " +
                        "местного самоуправления, включая сведения, составляющие служебную, коммерческую и " +
                        "банковскую тайну.", y, true);

        writeParagraph(cs, doc, regular,
                "В силу абз. 10 п. 1 ст. 20.3 Закона о банкротстве физические лица, юридические " +
                        "лица, государственные органы, органы управления государственными внебюджетными фондами " +
                        "Российской Федерации и органов местного самоуправления представляют запрошенные " +
                        "арбитражным управляющим сведения в течение семи дней со дня получения запроса без " +
                        "взимания платы.", y, true);

        writeParagraph(cs, doc, regular,
                "Согласно п. 7 ст. 213.9 Закона о банкротстве финансовый управляющий вправе " +
                        "получать информацию об имуществе гражданина, а также о счетах и вкладах (депозитах) " +
                        "гражданина, в том числе по банковским картам, об остатках электронных денежных средств " +
                        "и о переводах электронных денежных средств от граждан и юридических лиц (включая " +
                        "кредитные организации), от органов государственной власти, органов местного самоуправления.", y, true);
    }

    protected final void drawFooterParagraphs(PDPageContentStream[] cs, PDDocument doc,
                                              PDFont regular, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В случае отсутствия запрашиваемой информации прошу выдать соответствующую справку.", y, true);
        y[0] -= 5;

        String footer = "Запрашиваемую информацию прошу направить на имя финансового управляющего " +
                TRUSTEE_FULL_NAME_GENITIVE + ": " + TRUSTEE_MAIL_ADDRESS + ".";
        if (appendTrusteeEmailInFooter()) {
            footer += ", по адресу электронной почты: " + TRUSTEE_EMAIL;
        }
        writeParagraph(cs, doc, regular, footer, y, true);
    }

    protected final void drawAppendix(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                      PDFont bold, ClientDocumentRequest data, float[] y) throws IOException {
        y[0] -= 5;
        writeText(cs, doc, bold, "Приложение:", y);
        writeText(cs, doc, regular, "1. Копия Решения " + COURT_NAME_GENITIVE + " " +
                formatDate(data.caseDate()) + " по делу №" + data.caseNumber(), y);
        writeText(cs, doc, regular, "2. Копия паспорта должника;", y);
        writeText(cs, doc, regular, "3. Копия паспорта финансового управляющего", y);
        y[0] -= 20;
    }

    protected final void drawSignatures(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        PDFont bold, ClientDocumentRequest data, float[] y) throws IOException {
        writeText(cs, doc, regular, "Финансовый управляющий", y);
        writeText(cs, doc, bold,
                buildShortNameGenitive(data.genitive(), data.firstName(), data.middleName()), y);

        float trusteeWidth = bold.getStringWidth(TRUSTEE_SHORT_NAME) * FONT_SIZE / 1000;
        cs[0].beginText();
        cs[0].setFont(bold, FONT_SIZE);
        cs[0].newLineAtOffset(595.28f - RIGHT_MARGIN - trusteeWidth, y[0] + LINE_HEIGHT);
        cs[0].showText(TRUSTEE_SHORT_NAME);
        cs[0].endText();
    }

    // =====================================================================
    //                        ХЕЛПЕРЫ ДЛЯ ЗАПРОСОВ
    // =====================================================================

    /** "Подрезова Александра Александровича (07.09.1990 года рождения, ... адрес: ...)" */
    protected String debtorInfo(ClientDocumentRequest data, String fullName) {
        return fullName + " (" + formatDate(data.birthDate()) + " года рождения, место рождения " +
                data.birthPlace() + " ИНН " + data.inn() +
                ", СНИЛС " + data.snils() + ", адрес: " + buildDebtorAddress(data) + ")";
    }

    protected String debtorFullNameNominative(ClientDocumentRequest data) {
        return (data.lastName() + " " + data.firstName() + " " + data.middleName()).trim();
    }

    /** Стандартное вступление: "В целях выявления имущества Должника, ... прошу Вас: " */
    protected String standardRequestIntro() {
        return "В целях выявления имущества Должника, а так же проведения анализа финансового состояния " +
                "гражданина, заключения о наличии или об отсутствии оснований для оспаривания сделок должника, " +
                "прошу Вас: ";
    }

    /** Дата "за три года до возбуждения дела" для СУД */
    protected String threeYearsBeforeInitiationDate(ClientDocumentRequest data) {
        if (data.caseInitiationDate() != null) {
            return formatDate(data.caseInitiationDate().minusYears(3));
        }
        return FALLBACK_THREE_YEARS_DATE;
    }

    /** Год "за три года до возбуждения дела" для ОСФР / Росимущества */
    protected int threeYearsBeforeInitiationYear(ClientDocumentRequest data) {
        if (data.caseInitiationDate() != null) {
            return data.caseInitiationDate().getYear() - 3;
        }
        return FALLBACK_THREE_YEARS_YEAR;
    }

    protected String buildDebtorAddress(ClientDocumentRequest req) {
        List<String> parts = new ArrayList<>();
        addIfNotBlank(parts, req.postalCode());
        addIfNotBlank(parts, req.region());
        addIfNotBlank(parts, req.district());
        addIfNotBlank(parts, req.city());
        addIfNotBlank(parts, req.address());
        return String.join(", ", parts);
    }

    protected String buildShortNameGenitive(String genitiveFullName, String firstName, String middleName) {
        if (genitiveFullName == null || genitiveFullName.isBlank()) return "";
        String[] parts = genitiveFullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder(parts[0]);
        if (firstName != null && !firstName.isBlank()) {
            sb.append(" ").append(Character.toUpperCase(firstName.charAt(0))).append(".");
        }
        if (middleName != null && !middleName.isBlank()) {
            sb.append(Character.toUpperCase(middleName.charAt(0))).append(".");
        }
        return sb.toString();
    }

    protected String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "";
    }

    private void addIfNotBlank(List<String> parts, String value) {
        if (value != null && !value.isBlank()) parts.add(value.trim());
    }

    // =====================================================================
    //                        МЕТОДЫ РИСОВАНИЯ (без изменений)
    // =====================================================================

    private PDFont loadFont(PDDocument document, String path) throws IOException {
//        log.info("Загрузка шрифта: {}", path);
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new IOException(String.format(
                    "Шрифт не найден: '%s'. Проверьте, что файл существует в src/main/resources/%s", path, path));
        }
        try (is) {
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
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) newPage(csHolder, document, yHolder);
        text = sanitizeText(text);
        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(LEFT_MARGIN, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    protected void writeCenteredText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) newPage(csHolder, document, yHolder);
        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) * FONT_SIZE / 1000;
        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset((595.28f - textWidth) / 2, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    protected void writeRightText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) newPage(csHolder, document, yHolder);
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
                if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) newPage(csHolder, document, yHolder);
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
            if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) newPage(csHolder, document, yHolder);
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