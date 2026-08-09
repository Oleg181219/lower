package org.lower.document.exception;

/**
 * Ошибка взаимодействия с сервисом склонения ФИО.
 */
public class NameDeclensionException extends RuntimeException {

    public NameDeclensionException(String message) {
        super(message);
    }

    public NameDeclensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
