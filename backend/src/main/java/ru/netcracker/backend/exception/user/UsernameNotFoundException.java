package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.util.UserUtil;

public class UsernameNotFoundException extends ValidationException {
    public UsernameNotFoundException(String username) {
        super(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username));
    }
}
