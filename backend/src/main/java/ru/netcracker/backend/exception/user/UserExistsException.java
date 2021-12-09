package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.User;

public class UserExistsException extends ValidationException {
    public UserExistsException(final User user) {
        super(user.getUsername());
    }
}
