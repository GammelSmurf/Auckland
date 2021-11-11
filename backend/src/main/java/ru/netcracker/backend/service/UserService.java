package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.User;

public interface UserService {
    User banUser(String username) throws UserExistsException;
    User unbanUser(String username);
}
