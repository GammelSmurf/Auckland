package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.User;

public class UserExistsException extends ValidationException {
    public UserExistsException(final User user) {
        super(String.format("There is account with username: %s", user.getUsername()));
    }
}
