package org.lower.document.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Пустой конструктор для Jackson
public class WebAppChat {
    private Long id;
    private String type;
    private String title;
    private String username;
    private String photo_url; // или photoUrl, в зависимости от того, как приходит
}