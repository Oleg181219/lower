package org.lower.document.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientRequest {
    private String fullName;              // ФИО: "Подрезов Александр Александрович"
    private String fullNameShort;         // ФИО с инициалами: "Подрезов А.А."
    private LocalDate birthDate;          // Дата рождения: "07.09.1990"
    private String birthPlace;            // Место рождения: "Ростовская область, г. Новочеркасск"
    private String inn;                   // ИНН: "615018201246"
    private String snils;                 // СНИЛС: "163-409-915 71"
    private String address;
}
