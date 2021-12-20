package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.User;

public class EmailExistsException extends ValidationException {
    public EmailExistsException(User user) {
        super(String.format("There is no account with email: %s", user.getEmail()));
    }
}
