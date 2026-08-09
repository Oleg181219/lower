package org.lower.document.grpc;

import lombok.RequiredArgsConstructor;
import org.lower.document.dto.ClientFullNameForms;
import org.lower.document.dto.FullNameDeclensionDto;
import org.lower.document.dto.enums.PersonGender;
import org.lower.document.dto.request.FullNameFormsRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientFullNameService {

    private final NameDeclensionClient nameDeclensionClient;

    /**
     * Основной метод: 2 падежа из gRPC, короткое имя — локально.
     */
    public ClientFullNameForms buildFullNameForms(String lastName,
                                                  String firstName,
                                                  String middleName,
                                                  PersonGender gender) {

        // 1 и 2 формы — от Python-микросервиса через gRPC
        FullNameDeclensionDto d = nameDeclensionClient.declineFullName(
                lastName,
                firstName,
                middleName,
                gender != null ? gender : PersonGender.UNKNOWN
        );

        String middleGen = d.middleName() != null
                ? d.middleName().cases().genitive() : null;
        String middleIns = d.middleName() != null
                ? d.middleName().cases().instrumental() : null;

        // 3 форма — считается здесь, без gRPC
        String shortName = join(
                d.lastName().cases().nominative(),
                initials(firstName, middleName)
        );

        return new ClientFullNameForms(
                join(d.lastName().cases().genitive(),
                        d.firstName().cases().genitive(), middleGen),
                join(d.lastName().cases().instrumental(),
                        d.firstName().cases().instrumental(), middleIns),
                shortName
        );
    }

    /**
     * Удобная перегрузка, если на входе уже полный запрос документа.
     */
    public ClientFullNameForms buildFullNameForms(FullNameFormsRequest req) {
        return buildFullNameForms(req.lastName(), req.firstName(), req.middleName(), req.gender()
        );
    }

    /**
     * "Александр" + "Александрович" -> "А.А."
     */
    private static String initials(String firstName, String middleName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName.charAt(0)).append('.');
        }
        if (middleName != null && !middleName.isBlank()) {
            sb.append(middleName.charAt(0)).append('.');
        }
        return sb.toString();
    }

    private static String join(String... parts) {
        return Arrays.stream(parts)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" "));
    }
}