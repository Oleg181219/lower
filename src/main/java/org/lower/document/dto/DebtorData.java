package org.lower.document.dto;

public record DebtorData(
        String fullName,              // "Подрезов Александр Александрович"
        String fullNameGenitive,      // "Подрезова Александра Александровича"
        String fullNameShort,         // "Подрезов А.А."
        String fullNameShortGenitive, // "Подрезова А.А."
        String fullNameInstrumental,
        String birthDate,             // "07.09.1990"
        String birthPlace,            // "Ростовская область, г. Новочеркасск"
        String inn,                   // "615018201246"
        String snils,                 // "163-409-915 71"
        String address                // "346400, Ростовская область, г. Новочеркасск, сп. Красный, д. 6"
) {}
