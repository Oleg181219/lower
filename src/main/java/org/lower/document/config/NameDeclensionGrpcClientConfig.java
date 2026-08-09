package org.lower.document.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.lower.document.config.properties.NameDeclensionGrpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация gRPC-канала до сервиса склонения ФИО.
 */
@Configuration
@EnableConfigurationProperties(NameDeclensionGrpcProperties.class)
public class NameDeclensionGrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel nameDeclensionChannel(NameDeclensionGrpcProperties properties) {
        ManagedChannelBuilder<?> builder =
                ManagedChannelBuilder.forAddress(properties.host(), properties.port());

        if (properties.plaintext()) {
            builder.usePlaintext();
        }

        return builder.build();
    }
}
