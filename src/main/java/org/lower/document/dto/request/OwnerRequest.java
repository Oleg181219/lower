package org.lower.document.dto.request;

import lombok.Data;

@Data
public class OwnerRequest {
    private String password;
    private String fullName;        // "Степаньянц Светлана Анатольевна"
    private String fullNameShort;   // "Степаньянц С.А." (для подписи)
    private String mailAddress;     // "344082, г. Ростов-на-Дону, пер. Халтуринский, 4, оф. 6"
    private String email;           // "svetlanaot@yandex.ru"

    // 3. Данные СРО, в которой состоит юрист
    private String sroName;         // "СРО АУ \"Лига\""
    private String sroOgrn;         // "ОГРН 1045803007326"
    private String sroInn;          // "ИНН 5836140708"
    private String sroAddress;      // "г. Пенза, ул. Володарского, 9"

    // 4. Личные данные юриста
    private String inn;             // "ИНН 616511012560"
    private String snils;           // "СНИЛС 14063088738"
}
