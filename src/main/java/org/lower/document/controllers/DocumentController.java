package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.FspsNotificationRequest;
import org.lower.document.services.FspsNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/api/fsps")
@RequiredArgsConstructor
public class DocumentController {

    private FspsNotificationService notificationService;

    @PostMapping("/generate-and-send")
    public CompletableFuture<ResponseEntity<String>> generateAndSend(
            @RequestBody FspsNotificationRequest request) {

        return notificationService.processRequestAsync(request)
                .thenApply(v -> ResponseEntity.ok("Запрос-уведомление успешно сгенерировано и отправлено."))
                .exceptionally(ex -> {
                    log.error("Ошибка в асинхронной обработке", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Ошибка: " + ex.getMessage());
                });
    }

    // Для будущего: массовая генерация
    @PostMapping("/batch/generate-and-send")
    public CompletableFuture<ResponseEntity<String>> batchGenerateAndSend(
            @RequestBody List<FspsNotificationRequest> requests) {

        return notificationService.processMultipleRequestsAsync(requests)
                .thenApply(v -> ResponseEntity.ok("Пакет из " + requests.size() + " документов обработан."))
                .exceptionally(ex -> {
                    log.error("Ошибка в массовой обработке", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Ошибка пакетной обработки: " + ex.getMessage());
                });
    }
}