package org.lower.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientRequest {
    @NotBlank(message = "ФИО клиента не может быть пустым")
    private String fullName;

    @NotBlank(message = "ФИО клиента не может быть пустым")
    private String fullNameShort;

    @NotBlank(message = "ФИО клиента в родительном падеже не может быть пустым")
    private String fullNameGenitive;

    @NotNull(message = "Дата рождения обязательна")
    private LocalDate birthDate;

    @NotBlank(message = "Место рождения не может быть пустым")
    private String birthPlace;

    @NotBlank(message = "ИНН не может быть пустым")
    @Pattern(regexp = "^\\d{10}$|^\\d{12}$", message = "ИНН должен содержать 10 или 12 цифр")
    private String inn;

    @NotBlank(message = "СНИЛС не может быть пустым")
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{3} \\d{2}$", message = "СНИЛС должен быть в формате 000-000-000 00")
    private String snils;

    @NotBlank(message = "Адрес не может быть пустым")
    private String address;

    @NotBlank(message = "Регион не может быть пустым")
    private String region;

    private String courtName;
    private LocalDate decisionDate;
    private String caseNumber;
}