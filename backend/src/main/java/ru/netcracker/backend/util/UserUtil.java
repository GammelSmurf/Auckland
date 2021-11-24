package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.repository.UserRepository;

public class UserUtil {
    public static final String USER_NOT_FOUND_TEMPLATE = "There is no account with username: %s";
    public static final String EMAIL_NOT_FOUND_TEMPLATE = "THere is no account with email: %s";

    private UserUtil() {
    }

    public static void validate(User user, UserRepository userRepository) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailExistsException(String.format(EMAIL_NOT_FOUND_TEMPLATE, user.getEmail()));
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UserExistsException(String.format(USER_NOT_FOUND_TEMPLATE, user.getEmail()));
    }
}
