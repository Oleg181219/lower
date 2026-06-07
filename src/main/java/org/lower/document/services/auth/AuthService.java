package org.lower.document.services.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.lower.document.auth.JwtTokenProvider;
import org.lower.document.config.properties.AppProperties;
import org.lower.document.dao.OwnerDao;
import org.lower.document.dao.StaffDao;
import org.lower.document.dao.UserDao;
import org.lower.document.dto.request.OwnerRequest;
import org.lower.document.dto.response.OwnerResponse;
import org.lower.document.dto.request.StaffRequest;
import org.lower.document.dto.response.StaffResponse;
import org.lower.document.jooq.codegen.enums.RoleEnum;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.lower.document.util.UtilsAndConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserDao userDao;
    private final OwnerDao ownerDao;
    private final StaffDao staffDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    @Transactional
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

    /**
     * у нас @PreAuthorize используется, когда запрос приходит, JwtAuthenticationFilter валидирует JWT-токен,
     * достает из него username и role, и кладет их в SecurityContextHolder
     * Соответственно спринг сам из контекста проверит Админ или нет*/

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OwnerResponse createOwner(OwnerRequest ownerRequest) {
        UsersRecord newUser = userDao.createUser(ownerRequest, RoleEnum.OWNER);
        ownerDao.createNewOwner(ownerRequest, newUser);

        OwnerResponse response = new OwnerResponse();
        response.setResult(COMPLETE);
        return response;
    }

    public String authenticate(String username, String password) {
        UsersRecord user = userDao.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException(username);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Invalid auth data");
            throw new RuntimeException("Invalid auth data");
        }

        return jwtTokenProvider.generateToken(user);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public StaffResponse createStaff(StaffRequest request, Map<String, String> headers) {
        // 1. Узнаем, кто создает (текущий Owner)
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersRecord currentUser = userDao.findByUsername(currentUserName);

        // Находим его запись в таблице owners, чтобы получить owner_id
        OwnersRecord currentOwner = ownerDao.findByUsername(currentUserName);
        if (currentOwner == null) {
            throw new RuntimeException("Current user is not an owner");
        }

        // 2. Создаем запись в users (роль WORKER)
        UsersRecord newStaffUser = userDao.createUser(
                new OwnerRequest() {{ // Хак с наследованием, как ты и делал
                    setPassword(request.getPassword());
                    setEmail(request.getEmail());
                    setFullName(request.getFullName());
                    setFullNameShort(request.getFullNameShort());
                    setMailAddress(request.getMailAddress());
                    setSroName(request.getSroName());
                    setSroOgrn(request.getSroOgrn());
                    setSroInn(request.getSroInn());
                    setSroAddress(request.getSroAddress());
                    setInn(request.getInn());
                    setSnils(request.getSnils());
                }},
                RoleEnum.WORKER
        );
        staffDao.createStaff(request, currentOwner.getId(), newStaffUser.getId());
        return new StaffResponse();
    }
}