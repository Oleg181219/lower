package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientRosaviationDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override public String getDocType() { return "rosaviation"; }

    @Override protected String getRecipientOrgName() { return "ФЕДЕРАЛЬНОЕ АГЕНТСТВО ВОЗДУШНОГО ТРАНСПОРТА (РОСАВИАЦИЯ)"; }
    @Override protected String getRecipientOrgAddress() { return "125167, Ленинградский пр-т, д. 37, корп. 2, Москва"; }
    @Override protected String getRecipientOrgNote() { return null; }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                standardRequestIntro() +
                        "Предоставить сведения о воздушных судах, зарегистрированных/ снятых с учета за " +
                        debtorInfo(data, data.instrumental()) + ":", y, true);
    }
}