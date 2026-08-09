package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientRospatentDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "rospatent";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Федеральная служба по интеллектуальной собственности (Роспатент)";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "121059, г. Москва, Бережковская наб., 30, корп. 1";
    }

    @Override
    protected String getRecipientOrgNote() {
        return null;
    }

    /**
     * ВНИМАНИЕ: в исходном шаблоне текст запроса после "прошу Вас:" отсутствовал.
     * Формулировка восстановлена по аналогии с остальными документами — при необходимости замените.
     */
    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения об объектах интеллектуальной собственности (патентах, товарных знаках), " +
                        "зарегистрированных/ снятых с учета за " + debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}