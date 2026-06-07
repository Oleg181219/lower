package org.lower.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

// Этот record теперь идеально соответствует JSON-у от Telegram
public record WebAppUser(
        @JsonProperty("id") Long id,
        @JsonProperty("is_bot") Optional<Boolean> isBot, // is_bot тоже опционально
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") Optional<String> lastName,
        @JsonProperty("username") Optional<String> username,
        @JsonProperty("language_code") Optional<String> languageCode,
        @JsonProperty("is_premium") Optional<Boolean> isPremium,
        @JsonProperty("added_to_attachment_menu") Optional<Boolean> addedToAttachmentMenu,
        @JsonProperty("allows_write_to_pm") Optional<Boolean> allowsWriteToPm,
        @JsonProperty("photo_url") Optional<String> photoUrl
) {}