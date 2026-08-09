package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientMchsDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "mchs";
    }

    @Override
    protected String getRecipientOrgName() {
        return "«Главное Управление МЧС России по Ростовской области»";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344003, г. Ростов-на-Дону, ул. Города Волос, 11";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "Управление МЧС по региону прописки должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения о маломерных и иных судах, зарегистрированных/ снятых с учета за " +
                        debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}