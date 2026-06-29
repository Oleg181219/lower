package org.lower.document.dto;

public record RecipientData(
        String orgName,            // "Новочеркасский Городской отдел судебных приставов Ростовской области"
        String orgAddress,          // "346429, Ростовская обл., г. Новочеркасск, ул. Кавказская, 75"
        String orgNote
) {}
