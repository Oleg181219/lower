package org.lower.document.grpc;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.config.properties.NameDeclensionGrpcProperties;
import org.lower.document.dto.CaseFormsDto;
import org.lower.document.dto.DeclinedNameDto;
import org.lower.document.dto.FullNameDeclensionDto;
import org.lower.document.dto.enums.PersonGender;
import org.lower.document.exception.NameDeclensionException;
import org.lower.document.grpc.proto.CaseForms;
import org.lower.document.grpc.proto.DeclineFullNameRequest;
import org.lower.document.grpc.proto.DeclineFullNameResponse;
import org.lower.document.grpc.proto.DeclinedName;
import org.lower.document.grpc.proto.Gender;
import org.lower.document.grpc.proto.NameDeclensionServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * ТОЧКА ВХОДА для вызова внешнего Python-микросервиса склонения ФИО.
 * <p>
 * Использование:
 * <pre>
 *   FullNameDeclensionDto dto =
 *       nameDeclensionClient.declineFullName("Иванов", "Иван", "Иванович");
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NameDeclensionClient {

    private final ManagedChannel channel;
    private final NameDeclensionGrpcProperties properties;

    /**
     * Склонение ФИО без явного указания пола
     * (пол определит сам сервис по имени/отчеству).
     */
    public FullNameDeclensionDto declineFullName(String lastName,
                                                 String firstName,
                                                 String middleName) {
        return declineFullName(lastName, firstName, middleName, PersonGender.UNKNOWN);
    }

    /**
     * Склонение ФИО с явным указанием пола.
     */
    public FullNameDeclensionDto declineFullName(String lastName,
                                                 String firstName,
                                                 String middleName,
                                                 PersonGender gender) {

        validate(lastName, firstName);

        NameDeclensionServiceGrpc.NameDeclensionServiceBlockingStub stub =
                NameDeclensionServiceGrpc.newBlockingStub(channel)
                        .withDeadlineAfter(properties.deadlineMillis(), TimeUnit.MILLISECONDS);

        DeclineFullNameRequest request = DeclineFullNameRequest.newBuilder()
                .setLastName(lastName.trim())
                .setFirstName(firstName.trim())
                .setMiddleName(middleName == null ? "" : middleName.trim())
                .setGender(toProtoGender(gender))
                .build();

        try {
            DeclineFullNameResponse response = stub.declineFullName(request);
            return toDto(response);

        } catch (StatusRuntimeException e) {
            throw handleGrpcError(e);
        }
    }

    // ==================== private ====================

    private void validate(String lastName, String firstName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
    }

    private NameDeclensionException handleGrpcError(StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();

        String message = switch (code) {
            case UNAVAILABLE -> "Сервис склонения ФИО недоступен: " + properties.host() + ":" + properties.port();
            case DEADLINE_EXCEEDED -> "Сервис склонения ФИО не ответил за " + properties.deadlineMillis() + " мс";
            case INTERNAL -> "Внутренняя ошибка сервиса склонения ФИО";
            default -> "Ошибка gRPC-вызова склонения ФИО, код: " + code;
        };

        log.error("[name-declension] {}", message, e);
        return new NameDeclensionException(message, e);
    }

    private FullNameDeclensionDto toDto(DeclineFullNameResponse response) {
        return new FullNameDeclensionDto(
                toDto(response.getLastName()),
                toDto(response.getFirstName()),
                response.hasMiddleName() ? toDto(response.getMiddleName()) : null,
                fromProtoGender(response.getDetectedGender())
        );
    }

    private DeclinedNameDto toDto(DeclinedName declined) {
        return new DeclinedNameDto(toDto(declined.getCases()), declined.getConfidence());
    }

    private CaseFormsDto toDto(CaseForms cases) {
        return new CaseFormsDto(
                cases.getNominative(),
                cases.getGenitive(),
                cases.getDative(),
                cases.getAccusative(),
                cases.getInstrumental(),
                cases.getPrepositional()
        );
    }

    private Gender toProtoGender(PersonGender gender) {
        if (gender == null) {
            return Gender.GENDER_UNKNOWN;
        }
        return switch (gender) {
            case MALE -> Gender.GENDER_MALE;
            case FEMALE -> Gender.GENDER_FEMALE;
            case UNKNOWN -> Gender.GENDER_UNKNOWN;
        };
    }

    private PersonGender fromProtoGender(Gender gender) {
        return switch (gender) {
            case GENDER_MALE -> PersonGender.MALE;
            case GENDER_FEMALE -> PersonGender.FEMALE;
            default -> PersonGender.UNKNOWN;
        };
    }
}