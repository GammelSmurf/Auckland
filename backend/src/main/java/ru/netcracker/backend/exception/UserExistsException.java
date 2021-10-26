package ru.netcracker.backend.exception;

public class UserExistsException extends Throwable {
    public UserExistsException(final String message) {
        super(message);
    }
}
