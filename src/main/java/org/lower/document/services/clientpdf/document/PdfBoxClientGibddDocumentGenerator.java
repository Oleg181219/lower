package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientGibddDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "gibdd";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Управление ГИБДД ГУ МВД России по Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344103, г. Ростов-на-Дону, ул. Доватора, д. 154А";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "Управление МРЭО ГИБДД по региону регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения о транспортных средствах, зарегистрированных/ снятых с учета за " +
                        debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}