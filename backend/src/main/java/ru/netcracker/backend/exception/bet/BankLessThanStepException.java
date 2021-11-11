package ru.netcracker.backend.exception.bet;

public class BankLessThanStepException extends ValidationException{
    public BankLessThanStepException(final String message) {
        super(message);
    }
}
