package org.lower.document.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TelegramAuthData(
        String queryId,
        WebAppUser user,
        WebAppChat chat,
        long authDate,
        String signature,
        String hash
) {
}