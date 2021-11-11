package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User banUser(String username) throws UserExistsException;

    User unbanUser(String username);

    User addCurrency(String username, long currency);
}
