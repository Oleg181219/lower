package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientRosgvardiaDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "rosgvardia";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Управление Росгвардии по Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344038, г. Ростов-на-Дону, ул. Шеболдаева, 4/3";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "Управление Росгвардии по региону регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения о гражданском оружии, зарегистрированном/снятом с учета за " +
                        debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}