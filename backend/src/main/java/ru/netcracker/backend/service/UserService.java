package ru.netcracker.backend.service;

import ru.netcracker.backend.model.responses.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserResponse banUser(String username);

    UserResponse unbanUser(String username);

    UserResponse addMoney(String username, BigDecimal currency);
}
