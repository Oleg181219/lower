package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientRosimushchestvoDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "rosimushchestvo";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Территориальное управление Федерального агентства по управлению государственным имуществом (Росимущества) в Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344050 г. Ростов-на-Дону, ул. Социалистическая, д. 112";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "территориальный орган по области регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В целях выявления имущества Должника, а также проведения анализа финансового состояния " +
                        "гражданина прошу Вас:", y, true);

        writeParagraph(cs, doc, regular,
                "предоставить сведения о наличии в пользовании федерального имущества за период с " +
                        threeYearsBeforeInitiationYear(data) + " г. (три года до даты возбуждения дела о банкротстве) " +
                        "по настоящее время:", y, false);

        writeParagraph(cs, doc, regular,
                "за " + debtorInfo(data, data.instrumental()) + ":", y, false);
    }
}