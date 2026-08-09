package org.lower.document.services.clientpdf.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxClientFsspDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "fsps";
    }

    @Override
    protected String getDocumentTitle() {
        return "ЗАПРОС-УВЕДОМЛЕНИЕ";
    }

    @Override
    protected String getRecipientOrgName() {
        return "Новочеркасский Городской отдел судебных приставов Ростовской области";
    }

    @Override
    protected String getRecipientOrgAddress() {
        return "346429, Ростовская обл., г. Новочеркасск, ул. Кавказская, 75";
    }

    @Override
    protected String getRecipientOrgNote() {
        return "отдел судебных приставов по месту регистрации должника";
    }

    /**
     * ФССП: сначала два абзаца 229-ФЗ, затем стандартные
     */
    @Override
    protected void drawLegalBasisParagraphs(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                            ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "Согласно подпункту 7 пункта 1 статьи 47 Федерального закона от 02.10.2007 № 229-ФЗ " +
                        "«Об исполнительном производстве» исполнительное производство оканчивается судебным " +
                        "приставом-исполнителем в случае признания должника банкротом и направления исполнительного " +
                        "документа арбитражному управляющему, за исключением исполнительных документов, указанных " +
                        "в части 4 статьи 69.1 и части 4 статьи 96 настоящего Федерального закона.", y, true);

        writeParagraph(cs, doc, regular,
                "В соответствии с пунктом 4 статьи 69.1 Закона об исполнительном производстве " +
                        "при получении копии решения арбитражного суда о признании гражданина, в том числе " +
                        "индивидуального предпринимателя, банкротом и введении реализации имущества гражданина " +
                        "судебный пристав-исполнитель оканчивает исполнительное производство по исполнительным " +
                        "документам, за исключением исполнительных документов по требованиям об истребовании " +
                        "имущества из чужого незаконного владения, о признании права собственности, о взыскании " +
                        "алиментов, о взыскании задолженности по текущим платежам. Одновременно с окончанием " +
                        "исполнительного производства судебный пристав-исполнитель снимает наложенные им в ходе " +
                        "исполнительного производства аресты на имущество должника - гражданина, в том числе " +
                        "индивидуального предпринимателя, и иные ограничения распоряжения этим имуществом.", y, true);

        drawStandardLegalParagraphs(cs, doc, regular, y);
    }

    @Override
    protected void drawRequestParagraph(PDPageContentStream[] cs, PDDocument doc, PDFont regular,
                                        ClientDocumentRequest data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "На основании изложенного уведомляю об обязанности окончить исполнительные " +
                        "производства, возбужденные в отношении " + debtorInfo(data, data.genitive()) + ":", y, true);

        writeParagraph(cs, doc, regular,
                "и направить документы, подтверждающие окончание исполнительных производств " +
                        "(в том числе исполнительные листы) в адрес финансового управляющего.", y, false);
        y[0] -= 5;

        writeParagraph(cs, doc, regular,
                "Также прошу предоставить информацию о наличии исполнительных производств, " +
                        "в рамках которых взыскание производится в пользу " + data.genitive() + ".", y, true);
    }
}