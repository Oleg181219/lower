package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import org.lower.document.dao.OwnerDao;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {
    private final OwnerDao ownerDao;

    public OwnersRecord getOwnersRecord() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return ownerDao.findByUsername(currentUserName);
    }
}
