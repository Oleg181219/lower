package org.lower.document.services.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.lower.document.dto.FsspDocumentData;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfBoxRospatentDocumentGenerator extends AbstractPdfBoxDocumentGenerator {

    @Override
    public String getDocType() {
        return "rospatent";
    }

    @Override
    protected void drawDocumentTitle(PDPageContentStream[] cs, PDDocument doc, PDFont bold, float[] y) throws IOException {
        writeCenteredText(cs, doc, bold, "ЗАПРОС", y);
        y[0] -= 15;
    }

    @Override
    protected void drawBody(PDPageContentStream[] cs, PDDocument doc, PDFont regular, FsspDocumentData data, float[] y) throws IOException {
        writeParagraph(cs, doc, regular,
                "Решением " + data.courtDecision().courtName() + " от " +
                        data.courtDecision().decisionDate() + " по делу №" + data.courtDecision().caseNumber() +
                        " в отношении гражданина " + data.debtor().fullNameGenitive() +
                        " (" + data.debtor().birthDate() + " года рождения, место рождения " +
                        data.debtor().birthPlace() + " ИНН " + data.debtor().inn() +
                        ", СНИЛС " + data.debtor().snils() + ", адрес: " + data.debtor().address() +
                        ") введена процедура " + data.procedure() + ".", y, true);

        writeParagraph(cs, doc, regular,
                "Финансовым управляющим утвержден(а) " + data.trustee().fullName() +
                        " (ИНН " + data.trustee().inn() + ", СНИЛС " + data.trustee().snils() +
                        ") - " + data.trustee().sroName() + " (ОГРН " + data.trustee().sroOgrn() +
                        ", ИНН " + data.trustee().sroInn() + ", " + data.trustee().sroAddress() + ").", y, true);

        writeParagraph(cs, doc, regular,
                "В соответствии с абз. 7 п. 1 ст. 20.3 ФЗ «О несостоятельности (банкротстве)» " +
                        "арбитражному управляющему представлено право запрашивать необходимые сведения о должнике, " +
                        "о лицах, входящих в состав органов управления должника, о контролирующих лицах, о " +
                        "принадлежащем им имуществе (в том числе имущественных правах), о контрагентах и об " +
                        "обязательствах должника у физических лиц, юридических лиц, государственных органов, " +
                        "органов управления государственными внебюджетными фондами Российской Федерации и органов " +
                        "местного самоуправления, включая сведения, составляющие служебную, коммерческую и " +
                        "банковскую тайну.", y, true);

        writeParagraph(cs, doc, regular,
                "В силу абз. 10 п. 1 ст. 20.3 Закона о банкротстве физические лица, юридические " +
                        "лица, государственные органы, органы управления государственными внебюджетными фондами " +
                        "Российской Федерации и органы местного самоуправления представляют запрошенные " +
                        "арбитражному управляющим сведения в течение семи дней со дня получения запроса без " +
                        "взимания платы.", y, true);

        writeParagraph(cs, doc, regular,
                "Согласно п. 7 ст. 213.9 Закона о банкротстве финансовый управляющий вправе " +
                        "получать информацию об имуществе гражданина, а также о счетах и вкладах (депозитах) " +
                        "гражданина, в том числе по банковским картам, об остатках электронных денежных средств " +
                        "и о переводах электронных денежных средств от граждан и юридических лиц (включая " +
                        "кредитные организации), от органов государственной власти, органов местного самоуправления.", y, true);

        writeParagraph(cs, doc, regular,
                "В целях выявления имущества Должника, а так же проведения анализа финансового состояния " +
                        "гражданина, заключения о наличии или об отсутствии оснований для оспаривания сделок должника, " +
                        "прошу Вас: Предоставить сведения о результатах интеллектуальной деятельности, принадлежащих " +
                        getDebtorInstrumental(data) +
                        " (" + data.debtor().birthDate() + " года рождения, место рождения " +
                        data.debtor().birthPlace() + " ИНН " + data.debtor().inn() +
                        ", СНИЛС " + data.debtor().snils() + ", адрес: " + data.debtor().address() + "):", y, true);

        writeParagraph(cs, doc, regular,
                "В случае отсутствия запрашиваемой информации прошу выдать соответствующую справку.", y, true);

        y[0] -= 5;

        writeParagraph(cs, doc, regular,
                "Запрашиваемую информацию прошу направить на имя финансового управляющего " +
                        data.trustee().fullNameGenitive() + ": " + data.trustee().mailAddress() + ".", y, true);
    }

    private String getDebtorInstrumental(FsspDocumentData data) {
        if (data.debtor().fullNameInstrumental() != null && !data.debtor().fullNameInstrumental().isEmpty()) {
            return data.debtor().fullNameInstrumental();
        }
        return data.debtor().fullNameGenitive();
    }
}