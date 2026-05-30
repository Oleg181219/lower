package org.lower.document.dto;

import lombok.Data;

@Data
public class GeneratedFileDto {
    private String fileName; // Имя файл
    private String contentBase64; // Содержимое файла в кодировке Base64. вроде так надо передавать
}
