package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientSfrDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "sfr";
    }

    @Override
    protected String getRecipientOrgName() {
        return "ОСФР ПО РОСТОВСКОЙ ОБЛАСТИ";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "344000, обл. Ростовская, г. Ростов-На-Дону, ул. Варфоломеева, зд. 261/81";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "СФР России по региону регистрации должника";
    }

    /**
     * Только ОСФР добавляет e-mail в финальный абзац
     */
    @Override
    protected boolean appendTrusteeEmailInFooter() {
        return true;
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В целях выявления имущества Должника, а так же проведения анализа финансового состояния " +
                        "гражданина, заключения о наличии или об отсутствии оснований для оспаривания сделок должника, " +
                        "прошу Вас предоставить сведения о месте работы " + debtorInfo(data, data.genitive()) + ":", y, true);

        writeParagraph(cs, doc, regular,
                "- сведения о размере начисляемой заработной платы за период с " +
                        threeYearsBeforeInitiationYear(data) + " (три года до возбуждения дела о банкротстве) " +
                        "по текущую дату;", y, false);

        writeParagraph(cs, doc, regular,
                "- сведения о назначенных и выплачиваемых должнику пенсиях с " +
                        threeYearsBeforeInitiationYear(data) + " (три года до возбуждения дела о банкротстве).", y, false);
    }
}