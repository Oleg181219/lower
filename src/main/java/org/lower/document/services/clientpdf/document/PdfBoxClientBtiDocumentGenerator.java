package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientBtiDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override public String getDocType() { return "bti"; }

    @Override protected String getRecipientOrgName() {
        return "Ростовский филиал АО «Ростехинвентаризация - Федеральное БТИ»";
    }
    @Override protected String getRecipientOrgAddress() { return "344082, г. Ростов-на-Дону, ул. Береговая, 15"; }
    @Override protected String getRecipientOrgNote() {
        return "муниципальное БТИ по месту регистрации должника. Ликвидируются, полномочия делегируются в другие организации";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения об объектах недвижимого имущества, зарегистрированных/ " +
                        "снятых с учета за " + debtorInfo(data, data.instrumental()) + ".", y, true);
    }
}