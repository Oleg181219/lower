package org.lower.document.services.document;

import org.lower.document.dto.FsspDocumentData;

import java.io.IOException;

/**
 * Интерфейс генератора PDF документов.
 * Позволяет легко менять реализацию (PDFBox, iText, OpenPDF и т.д.)
 */
public interface PdfDocumentGenerator {

    /**
     * Генерирует PDF документ на основе данных
     * @param data данные для заполнения документа
     * @return байты PDF файла
     */
    byte[] generate(FsspDocumentData data) throws IOException;
}
