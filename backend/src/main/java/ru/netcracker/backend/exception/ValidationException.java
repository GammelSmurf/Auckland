package ru.netcracker.backend.exception;

public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }
}
