package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.GeneratedFileDto;
import org.lower.document.dto.request.BatchGenerationRequest;
import org.lower.document.dto.response.ClientsResonse;
import org.lower.document.services.ClientService;
import org.lower.document.services.PdfGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final ClientService clientService;
    private final PdfGenerationService pdfGenerationService;

    /**
     * Эндпоинт для получения списков клиентов по владельцу(айдишка владельца через анализ токена).
     */
    @GetMapping("/getClients")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ClientsResonse> getClients() {
        return ResponseEntity.ok(clientService.getClients());
    }

    /**
     * Эндпоинт для генерации документов.
     * Возвращает JSON массив с файлами (имя + base64 контент).
     * Фронтенд сам решает, скачивать их по отдельности или упаковать в ZIP.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<GeneratedFileDto>> generateDocuments(@RequestBody BatchGenerationRequest request) {

        List<GeneratedFileDto> files = pdfGenerationService.generateDocuments(request);

        return ResponseEntity.ok(files);
    }
}