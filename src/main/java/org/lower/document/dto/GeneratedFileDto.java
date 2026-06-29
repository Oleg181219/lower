package org.lower.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneratedFileDto {
    private String fileName; // Имя файл
    private String contentBase64; // Содержимое файла в кодировке Base64. вроде так надо передавать
}
