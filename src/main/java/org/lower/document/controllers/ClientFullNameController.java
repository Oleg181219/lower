package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.ClientFullNameForms;
import org.lower.document.dto.request.ClientDocumentRequest;
import org.lower.document.dto.request.FullNameFormsRequest;
import org.lower.document.grpc.ClientFullNameService;
import org.lower.document.services.PdfClientGenerationService;
import org.lower.document.util.TimeProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientFullNameController {
    private final PdfClientGenerationService pdfClientGenerationService;
    private final ClientFullNameService clientFullNameService;
    private final TimeProvider timeProvider;


    @PostMapping("/full-name-forms")
    public ResponseEntity<ClientFullNameForms> getFullNameForms(@RequestBody FullNameFormsRequest request) {
        return ResponseEntity.ok(clientFullNameService.buildFullNameForms(
                request.lastName(),
                request.firstName(),
                request.middleName(),
                request.gender()
        ));
    }

    @PostMapping("/generate_doc")
    public ResponseEntity<StreamingResponseBody> generateDocuments(
            @RequestBody ClientDocumentRequest request) {

        // Формируем имя файла с текущей датой/временем
        String fileName = String.format("documents_%s.zip",
                timeProvider.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        // StreamingResponseBody будет записывать данные прямо в HTTP-ответ
        StreamingResponseBody responseBody = outputStream ->
                pdfClientGenerationService.generateAndStreamToZip(request, outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(responseBody);
    }
}