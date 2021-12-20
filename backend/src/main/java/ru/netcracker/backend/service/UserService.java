package ru.netcracker.backend.service;

import ru.netcracker.backend.model.requests.UserRequest;
import ru.netcracker.backend.model.responses.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserResponse banUser(String username);

    UserResponse unbanUser(String username);

    UserResponse addMoney(String username, BigDecimal money);

    UserResponse updateUser(UserRequest userRequest);

    BigDecimal getMoneyByUsername(String username);
}
