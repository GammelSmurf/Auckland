package ru.netcracker.backend.exception.bet;

import ru.netcracker.backend.exception.ValidationException;

public class BankLessThanOldException extends ValidationException {
    public BankLessThanOldException(final String message) {
        super(message);
    }
}
