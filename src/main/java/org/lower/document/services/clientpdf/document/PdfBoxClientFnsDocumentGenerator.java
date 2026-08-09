package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientFnsDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "fns";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Межрайонная инспекция Федеральной налоговой службы № 13 по Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "346407, Ростовская область, г. Новочеркасск, пр. Ермака, 104";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "налоговая по месту регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В целях проведения анализа финансового состояния гражданина прошу Вас предоставить следующую " +
                        "информацию в отношении гражданина " + debtorInfo(data, data.genitive()) + ":", y, true);

        writeParagraph(cs, doc, regular, "- сведения о наличии/отсутствии статуса индивидуального предпринимателя;", y, false);
        writeParagraph(cs, doc, regular, "- сведения о юридических лицах, учредителем (акционером) которых является " +
                debtorFullNameNominative(data) + ";", y, false);
        writeParagraph(cs, doc, regular, "- сведения о наличии (отсутствии) задолженности по обязательным платежам.", y, false);
        writeParagraph(cs, doc, regular, "- сведения о доходах должника за трехлетний период.", y, false);
    }
}