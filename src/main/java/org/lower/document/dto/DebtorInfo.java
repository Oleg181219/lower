package org.lower.document.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DebtorInfo {
    private String fullName;
    private String fullNameGenitive;
    private String fullNameShortGenitive;
    private String fullNameShort;
    private LocalDate birthDate;
    private String birthPlace;
    private String inn;
    private String snils;
    private String address;
}
