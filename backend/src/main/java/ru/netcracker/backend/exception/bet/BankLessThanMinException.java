package ru.netcracker.backend.exception.bet;

public class BankLessThanMinException extends ValidationException {
    public BankLessThanMinException(final String message) {
        super(message);
    }
}
