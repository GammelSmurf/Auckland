package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;

public class NotSubscribedException extends ValidationException {
    public NotSubscribedException(String message) {
        super(message);
    }
}
