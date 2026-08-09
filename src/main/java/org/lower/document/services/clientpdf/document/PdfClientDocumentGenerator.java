package org.lower.document.services.clientpdf.document;


import org.lower.document.dto.request.ClientDocumentRequest;

import java.io.IOException;

public interface PdfClientDocumentGenerator {

    byte[] generate(ClientDocumentRequest data) throws IOException;

    String getDocType();
}