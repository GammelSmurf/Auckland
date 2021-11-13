package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.responses.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserResponse banUser(String username) throws UserExistsException;

    UserResponse unbanUser(String username);

    UserResponse addCurrency(String username, BigDecimal currency);
}
