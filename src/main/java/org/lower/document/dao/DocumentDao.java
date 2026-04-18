package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import javax.swing.text.Document;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentDao{
    private final DSLContext dsl;
    public void insert(Document document) {

    }
}
