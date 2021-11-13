package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;

public class NoLotsException extends ValidationException {
    public NoLotsException(String message) {
        super(message);
    }
}
