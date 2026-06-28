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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class PdfBoxFsspDocumentGenerator implements PdfDocumentGenerator {

    private static final String FONT_REGULAR_PATH = "/fonts/PTSerif-Regular.ttf";
    private static final String FONT_BOLD_PATH = "/fonts/PTSerif-Bold.ttf";

    private static final float FONT_SIZE = 11f;
    private static final float LINE_HEIGHT = 15f;
    private static final float LEFT_MARGIN = 70f;
    private static final float RIGHT_MARGIN = 70f;
    private static final float TOP_MARGIN = 60f;
    private static final float BOTTOM_MARGIN = 50f;
    private static final float PAGE_WIDTH = 595.28f - LEFT_MARGIN - RIGHT_MARGIN;
    private static final float PAGE_HEIGHT = 841.89f;

    @Override
    public byte[] generate(FsspDocumentData data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont regular = loadFont(document, FONT_REGULAR_PATH);
            PDFont bold = loadFont(document, FONT_BOLD_PATH);

            // Массив для хранения текущего stream (чтобы передавать по ссылке)
            PDPageContentStream[] csHolder = new PDPageContentStream[1];
            csHolder[0] = new PDPageContentStream(document, page);

            float[] yHolder = new float[1];
            yHolder[0] = PAGE_HEIGHT - TOP_MARGIN;

            try {
                // ШАПКА (данные АУ + получатель)
                drawHeader(csHolder, document, regular, bold, data, yHolder);

                // Заголовок
                writeCenteredText(csHolder, document, bold, "ЗАПРОС-УВЕДОМЛЕНИЕ", yHolder);
                yHolder[0] -= 15;

                // ОСНОВНОЙ ТЕКСТ
                writeParagraph(csHolder, document, regular,
                        "Решением " + data.courtDecision().courtName() + " от " +
                                data.courtDecision().decisionDate() + " по делу №" + data.courtDecision().caseNumber() +
                                " в отношении гражданина " + data.debtor().fullNameGenitive() +
                                " (" + data.debtor().birthDate() + " года рождения, место рождения " +
                                data.debtor().birthPlace() + " ИНН " + data.debtor().inn() +
                                ", СНИЛС " + data.debtor().snils() + ", адрес: " + data.debtor().address() +
                                ") введена процедура " + data.procedure() + ".", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "Финансовым управляющим утвержден(а) " + data.trustee().fullName() +
                                " (ИНН " + data.trustee().inn() + ", СНИЛС " + data.trustee().snils() +
                                ") - " + data.trustee().sroName() + " (ОГРН " + data.trustee().sroOgrn() +
                                ", ИНН " + data.trustee().sroInn() + ", " + data.trustee().sroAddress() + ").", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "Согласно подпункту 7 пункта 1 статьи 47 Федерального закона от 02.10.2007 № 229-ФЗ " +
                                "«Об исполнительном производстве» исполнительное производство оканчивается судебным " +
                                "приставом-исполнителем в случае признания должника банкротом и направления исполнительного " +
                                "документа арбитражному управляющему, за исключением исполнительных документов, указанных " +
                                "в части 4 статьи 69.1 и части 4 статьи 96 настоящего Федерального закона.", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "В соответствии с пунктом 4 статьи 69.1 Закона об исполнительном производстве " +
                                "при получении копии решения арбитражного суда о признании гражданина, в том числе " +
                                "индивидуального предпринимателя, банкротом и введении реализации имущества гражданина " +
                                "судебный пристав-исполнитель оканчивает исполнительное производство по исполнительным " +
                                "документам, за исключением исполнительных документов по требованиям об истребовании " +
                                "имущества из чужого незаконного владения, о признании права собственности, о взыскании " +
                                "алиментов, о взыскании задолженности по текущим платежам. Одновременно с окончанием " +
                                "исполнительного производства судебный пристав-исполнитель снимает наложенные им в ходе " +
                                "исполнительного производства аресты на имущество должника - гражданина, в том числе " +
                                "индивидуального предпринимателя, и иные ограничения распоряжения этим имуществом.", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "В соответствии с абз. 7 п. 1 ст. 20.3 ФЗ «О несостоятельности (банкротстве)» " +
                                "арбитражному управляющему представлено право запрашивать необходимые сведения о должнике, " +
                                "о лицах, входящих в состав органов управления должника, о контролирующих лицах, о " +
                                "принадлежащем им имуществе (в том числе имущественных правах), о контрагентах и об " +
                                "обязательствах должника у физических лиц, юридических лиц, государственных органов, " +
                                "органов управления государственными внебюджетными фондами Российской Федерации и органов " +
                                "местного самоуправления, включая сведения, составляющие служебную, коммерческую и " +
                                "банковскую тайну.", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "В силу абз. 10 п. 1 ст. 20.3 Закона о банкротстве физические лица, юридические " +
                                "лица, государственные органы, органы управления государственными внебюджетными фондами " +
                                "Российской Федерации и органы местного самоуправления представляют запрошенные " +
                                "арбитражному управляющим сведения в течение семи дней со дня получения запроса без " +
                                "взимания платы.", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "Согласно п. 7 ст. 213.9 Закона о банкротстве финансовый управляющий вправе " +
                                "получать информацию об имуществе гражданина, а также о счетах и вкладах (депозитах) " +
                                "гражданина, в том числе по банковским картам, об остатках электронных денежных средств " +
                                "и о переводах электронных денежных средств от граждан и юридических лиц (включая " +
                                "кредитные организации), от органов государственной власти, органов местного самоуправления.", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "На основании изложенного уведомляю об обязанности окончить исполнительные " +
                                "производства, возбужденные в отношении " + data.debtor().fullNameGenitive() +
                                " (" + data.debtor().birthDate() + " года рождения, место рождения " +
                                data.debtor().birthPlace() + " ИНН " + data.debtor().inn() +
                                ", СНИЛС " + data.debtor().snils() + ", адрес: " + data.debtor().address() + "):", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "и направить документы, подтверждающие окончание исполнительных производств " +
                                "(в том числе исполнительные листы) в адрес финансового управляющего.", yHolder, false);

                yHolder[0] -= 5;

                writeParagraph(csHolder, document, regular,
                        "Также прошу предоставить информацию о наличии исполнительных производств, " +
                                "в рамках которых взыскание производится в пользу " + data.debtor().fullNameGenitive() + ".", yHolder, true);

                writeParagraph(csHolder, document, regular,
                        "В случае отсутствия запрашиваемой информации прошу выдать соответствующую справку.", yHolder, true);

                yHolder[0] -= 5;

                writeParagraph(csHolder, document, regular,
                        "Запрашиваемую информацию прошу направить на имя финансового управляющего " +
                                data.trustee().fullNameGenitive() + ": " + data.trustee().mailAddress() + ".", yHolder, true);

                yHolder[0] -= 10;

                // Приложение
                writeText(csHolder, document, bold, "Приложение:", yHolder);
                writeText(csHolder, document, regular, "1. Копия Решения " + data.courtDecision().courtName() + " " +
                        data.courtDecision().decisionDate() + " по делу №" + data.courtDecision().caseNumber(), yHolder);
                writeText(csHolder, document, regular, "2. Копия паспорта должника;", yHolder);
                writeText(csHolder, document, regular, "3. Копия паспорта финансового управляющего", yHolder);

                yHolder[0] -= 20;

                // Подписи
                writeText(csHolder, document, regular, "Финансовый управляющий", yHolder);
                writeText(csHolder, document, bold, data.debtor().fullNameShortGenitive(), yHolder);

                // Правая подпись
                String trusteeShort = data.trustee().fullNameShort();
                float trusteeWidth = bold.getStringWidth(trusteeShort) * FONT_SIZE / 1000;
                csHolder[0].beginText();
                csHolder[0].setFont(bold, FONT_SIZE);
                csHolder[0].newLineAtOffset(595.28f - RIGHT_MARGIN - trusteeWidth, yHolder[0] + LINE_HEIGHT);
                csHolder[0].showText(trusteeShort);
                csHolder[0].endText();

            } finally {
                // Закрываем stream перед сохранением
                if (csHolder[0] != null) {
                    csHolder[0].close();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
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

    /**
     * Рисует шапку документа:
     * - Данные АУ (по центру)
     * - Горизонтальная линия
     * - Данные получателя (по правому краю)
     */
    private void drawHeader(PDPageContentStream[] csHolder, PDDocument document,
                            PDFont fontRegular, PDFont fontBold,
                            FsspDocumentData data, float[] yHolder) throws IOException {

        // Данные АУ (по центру)
        writeCenteredText(csHolder, document, fontBold, "Финансовый управляющий", yHolder);
        writeCenteredText(csHolder, document, fontBold, data.trustee().fullName(), yHolder);
        writeCenteredText(csHolder, document, fontRegular,
                "адрес для направления корреспонденции: " + data.trustee().mailAddress(), yHolder);
        writeCenteredText(csHolder, document, fontRegular,
                "адрес электронной почты: " + data.trustee().email(), yHolder);
        yHolder[0] -= 10;

        // Горизонтальная линия
        csHolder[0].moveTo(LEFT_MARGIN, yHolder[0]);
        csHolder[0].lineTo(595.28f - RIGHT_MARGIN, yHolder[0]);
        csHolder[0].stroke();
        yHolder[0] -= 15;

        // Данные получателя (ПО ПРАВОМУ КРАЮ)
        writeRightText(csHolder, document, fontBold, data.recipient().orgName(), yHolder);
        writeRightText(csHolder, document, fontRegular, data.recipient().orgAddress(), yHolder);
        writeRightText(csHolder, document, fontRegular,
                "(отдел судебных приставов по месту регистрации должника)", yHolder);
        yHolder[0] -= 20;
    }

    /**
     * Создает новую страницу и обновляет stream
     */
    private void newPage(PDPageContentStream[] csHolder, PDDocument document, float[] yHolder) throws IOException {
        csHolder[0].close();
        PDPage newPage = new PDPage(PDRectangle.A4);
        document.addPage(newPage);
        csHolder[0] = new PDPageContentStream(document, newPage);
        yHolder[0] = PAGE_HEIGHT - TOP_MARGIN;
    }

    private void writeText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(LEFT_MARGIN, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    private void writeCenteredText(PDPageContentStream[] csHolder, PDDocument document, PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        float textWidth = font.getStringWidth(text) * FONT_SIZE / 1000;
        float x = (595.28f - textWidth) / 2;

        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(x, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    /**
     * Пишет текст, выровненный по правому краю
     */
    private void writeRightText(PDPageContentStream[] csHolder, PDDocument document,
                                PDFont font, String text, float[] yHolder) throws IOException {
        if (yHolder[0] < BOTTOM_MARGIN + LINE_HEIGHT) {
            newPage(csHolder, document, yHolder);
        }

        float textWidth = font.getStringWidth(text) * FONT_SIZE / 1000;
        float x = 595.28f - RIGHT_MARGIN - textWidth;

        // Защита от выхода за левый край
        if (x < LEFT_MARGIN) x = LEFT_MARGIN;

        csHolder[0].beginText();
        csHolder[0].setFont(font, FONT_SIZE);
        csHolder[0].newLineAtOffset(x, yHolder[0]);
        csHolder[0].showText(text);
        csHolder[0].endText();
        yHolder[0] -= LINE_HEIGHT;
    }

    private void writeParagraph(PDPageContentStream[] csHolder, PDDocument document,
                                PDFont font, String text, float[] yHolder, boolean indent) throws IOException {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float indentSize = indent ? 35f : 0;

        for (String word : words) {
            String testLine = !line.isEmpty() ? line + " " + word : word;
            float lineWidth = 0;
            try {
                lineWidth = font.getStringWidth(testLine) * FONT_SIZE / 1000;
            } catch (Exception e) {
                log.error("testLine " + testLine + " " + word);
            }

            if (lineWidth > PAGE_WIDTH && !line.isEmpty()) {
                // Проверка на новую страницу
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

        // Последняя строка
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
}