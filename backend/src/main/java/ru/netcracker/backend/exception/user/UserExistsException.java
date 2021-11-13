package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;

public class UserExistsException extends ValidationException {
    public UserExistsException(final String message) {
        super(message);
    }
}
