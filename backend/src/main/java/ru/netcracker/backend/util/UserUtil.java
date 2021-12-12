package ru.netcracker.backend.util;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.repository.UserRepository;

public class UserUtil {
    public static final String EMAIL_NOT_FOUND_TEMPLATE = "THere is no account with email: %s";

    private UserUtil() {
    }

    public static void validate(User user, UserRepository userRepository) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailExistsException(String.format(EMAIL_NOT_FOUND_TEMPLATE, user.getEmail()));
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameNotFoundException(user.getUsername());
    }
}
