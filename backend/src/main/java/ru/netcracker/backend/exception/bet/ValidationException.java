package ru.netcracker.backend.exception.bet;

public class ValidationException extends Throwable{
    public ValidationException(final String message) {
        super(message);
    }
}
