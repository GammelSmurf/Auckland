package ru.netcracker.backend.exception.bet;

public class BankLessThanOldException extends ValidationException {
    public BankLessThanOldException(final String message) {
        super(message);
    }
}
