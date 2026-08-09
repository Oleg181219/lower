package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientCourtDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "court";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Шахтинский городской суд Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "346500, Ростовская область, г. Шахты, ул. Черенкова, д. 17 А";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "районный/городской суд по месту регистрации должника";
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "В целях проведения анализа финансового состояния гражданина прошу Вас предоставить следующую " +
                        "информацию в отношении гражданина " + debtorInfo(data, data.genitive()) + ":", y, true);

        writeParagraph(cs, doc, regular,
                "- о наличии судебных разбирательств с участием должника в период, начиная с " +
                        threeYearsBeforeInitiationDate(data) + "г. (за три года до возбуждения дела о банкротстве) " +
                        "по текущую дату.", y, true);

        writeParagraph(cs, doc, regular,
                "В целях обнаружения у должника дебиторской задолженности прошу предоставить сведения о " +
                        "наличии/отсутствии в производстве суда дел, по которым ФИО и супруг(а) должника " +
                        "(при наличии зарегистрированного или расторгнутого в трехлетний период до возбуждения " +
                        "дела о банкротстве брака, ФИО супруги, паспортные данные супруги), являются взыскателями.", y, true);
    }
}