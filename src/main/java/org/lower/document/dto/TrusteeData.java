package org.lower.document.dto;

public record TrusteeData(
        String fullName,           // "Степаньянц Светлана Анатольевна"
        String fullNameShort,      // "Степаньянц С.А."
        String fullNameGenitive,   // "Степаньянц Светланы Анатольевны"
        String mailAddress,        // "344082, г. Ростов-на-Дону, пер. Халтуринский, 4, оф. 6"
        String email,              // "svetlanaot@yandex.ru"
        String inn,                // "616511012560"
        String snils,              // "14063088738"
        String sroName,            // "СРО АУ \"Лига\""
        String sroOgrn,            // "1045803007326"
        String sroInn,             // "5836140708"
        String sroAddress          // "г. Пенза, ул. Володарского, 9"
) {}
