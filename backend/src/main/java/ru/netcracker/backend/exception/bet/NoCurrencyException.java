package ru.netcracker.backend.exception.bet;

import ru.netcracker.backend.exception.ValidationException;

public class NoCurrencyException extends ValidationException {
    public NoCurrencyException(String message) {
        super(message);
    }
}
