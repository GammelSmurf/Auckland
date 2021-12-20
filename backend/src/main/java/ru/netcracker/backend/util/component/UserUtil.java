package ru.netcracker.backend.util.component;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.repository.UserRepository;

@Component
public class UserUtil {
    public void validate(User user, UserRepository userRepository) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailExistsException(user);
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameNotFoundException(user.getUsername());
    }
}
