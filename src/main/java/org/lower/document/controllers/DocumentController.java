package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.GeneratedFileDto;
import org.lower.document.dto.request.BatchGenerationRequest;
import org.lower.document.services.PdfGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final PdfGenerationService pdfGenerationService;

    /**
     * Эндпоинт для генерации документов.
     * Возвращает JSON массив с файлами (имя + base64 контент).
     * Фронтенд сам решает, скачивать их по отдельности или упаковать в ZIP.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<GeneratedFileDto>> generateDocuments(@RequestBody BatchGenerationRequest request) {

        List<GeneratedFileDto> files = pdfGenerationService.generateDocuments(request);

        return ResponseEntity.ok(files);
    }
}