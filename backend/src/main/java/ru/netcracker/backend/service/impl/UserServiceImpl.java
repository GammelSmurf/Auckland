package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.User;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User banUser(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("There is no account with username: " + username);
        }
        user.setIsBanned(true);
        return userRepository.save(user);
    }

    @Override
    public User unbanUser(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("There is no account with username: " + username);
        }
        user.setIsBanned(false);
        return userRepository.save(user);
    }
}
