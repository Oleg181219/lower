package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.request.BatchGenerationRequest;
import org.lower.document.dto.response.ClientsResonse;
import org.lower.document.dto.response.CourtDecisionsResponse;
import org.lower.document.dto.response.OrgResponse;
import org.lower.document.services.ClientService;
import org.lower.document.services.CourtDecisionsService;
import org.lower.document.services.OrgService;
import org.lower.document.services.PdfGenerationService;
import org.lower.document.util.TimeProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final ClientService clientService;
    private final OrgService orgService;
    private final CourtDecisionsService courtDecisionsService;
    private final PdfGenerationService pdfGenerationService;
    private final TimeProvider timeProvider;


    /**
     * Эндпоинт для получения списков клиентов по владельцу(айдишка владельца через анализ токена).
     */
    @GetMapping("/getClients")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ClientsResonse> getClients() {
        return ResponseEntity.ok(clientService.getClients());
    }

    /**
     * Эндпоинт для получения списков клиентов по владельцу(айдишка владельца через анализ токена).
     */
    @GetMapping("/getOrganizations/{region}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<OrgResponse>> getOrganizations(@PathVariable String region) {
        return ResponseEntity.ok(orgService.getOrganizations(region));
    }

    /**
     * Эндпоинт для получения списка дел клиента по клиенту и владельцу(айдишка владельца через анализ токена).
     */
    @GetMapping("/getCourtDecisions/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<CourtDecisionsResponse>> getCourtDecisions(@PathVariable UUID id) {
        return ResponseEntity.ok(courtDecisionsService.getCourtDecisions(id));
    }

    /**
     * Эндпоинт для генерации документов.
     * Возвращает JSON массив с файлами (имя + base64 контент).
     * Фронтенд сам решает, скачивать их по отдельности или упаковать в ZIP.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<StreamingResponseBody> generateDocuments(
            @RequestBody BatchGenerationRequest request) {

        // Формируем имя файла с текущей датой/временем
        String fileName = String.format("documents_%s.zip",
                timeProvider.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        // StreamingResponseBody будет записывать данные прямо в HTTP-ответ
        StreamingResponseBody responseBody = outputStream ->
                pdfGenerationService.generateAndStreamToZip(request, outputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(responseBody);
    }
}