package org.lower.document.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Настройки подключения к Python-микросервису склонения ФИО.
 */
@ConfigurationProperties(prefix = "name-declension.grpc")
public record NameDeclensionGrpcProperties(

        @DefaultValue("localhost") String host,

        @DefaultValue("50051") int port,

        // true = без TLS (локально / docker-сеть)
        @DefaultValue("true") boolean plaintext,

        // таймаут одного вызова, мс
        @DefaultValue("5000") long deadlineMillis
) {
}