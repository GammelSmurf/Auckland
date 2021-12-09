package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;

public class UsernameNotFoundException extends ValidationException {
    public UsernameNotFoundException(String username) {
        super(String.format("There is no account with username: %s", username));
    }
}
