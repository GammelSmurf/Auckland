package ru.netcracker.backend.exception.bet;

import ru.netcracker.backend.exception.ValidationException;

public class BankLessThanMinException extends ValidationException {
    public BankLessThanMinException(final String message) {
        super(message);
    }
}
