package ru.netcracker.backend.exception.bet;

import ru.netcracker.backend.exception.ValidationException;

public class BankLessThanStepException extends ValidationException {
    public BankLessThanStepException(final String message) {
        super(message);
    }
}
