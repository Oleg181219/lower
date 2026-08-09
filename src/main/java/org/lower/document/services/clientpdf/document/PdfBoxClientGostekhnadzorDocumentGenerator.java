package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientGostekhnadzorDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "gostekhnadzor";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Управление государственного надзора за техническим состоянием самоходных машин и других видов техники Ростовской области Ростовоблгостехнадзор";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344038, г. Ростов-на-Дону, пр. Михаила Нагибина, 14 А";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "Управление гостехнадзора по региону регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения о самоходных транспортных средствах, сельскохозяйственной и иной технике, " +
                        "зарегистрированных/ снятых с учета за " + debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}