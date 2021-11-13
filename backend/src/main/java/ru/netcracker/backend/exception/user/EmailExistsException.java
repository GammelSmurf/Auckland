package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;

public class EmailExistsException extends ValidationException {
    public EmailExistsException(final String message) {
        super(message);
    }
}
