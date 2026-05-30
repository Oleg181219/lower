package org.lower.document.services.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.auth.JwtTokenProvider;
import org.lower.document.config.properties.AppProperties;
import org.lower.document.dao.OwnerDao;
import org.lower.document.dao.UserDao;
import org.lower.document.dto.OwnerRequest;
import org.lower.document.dto.OwnerResponse;
import org.lower.document.dto.StaffRequest;
import org.lower.document.dto.StaffResponse;
import org.lower.document.jooq.codegen.enums.RoleEnum;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.lower.document.util.UtilsAndConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserDao userDao;
    private final OwnerDao ownerDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    @PreAuthorize("hasRole('ADMIN')")
    public OwnerResponse createOwner(OwnerRequest ownerRequest, Map<String, String> headers) {
        OwnerResponse ownerResponse = new OwnerResponse();
        if (appProperties.getName().equalsIgnoreCase(headers.get(ADMIN))) {
            UsersRecord newUser = userDao.createUser(ownerRequest, RoleEnum.OWNER);
            ownerDao.createNewOwner(ownerRequest, newUser);
            ownerResponse.setResult(COMPLETE);
        } else {
            ownerResponse.setResult(WRONG_FORMAT);
        }
        return ownerResponse;
    }

    public String authenticate(String username, String password) {
        UsersRecord user = userDao.findByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Invalid auth data");
            throw new RuntimeException("Invalid auth data");
        }

        return jwtTokenProvider.generateToken(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public StaffResponse createStaff(StaffRequest request, Map<String, String> headers) {

        return null;
    }
}