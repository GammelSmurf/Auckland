package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.requests.UserRequest;
import ru.netcracker.backend.model.responses.ContactInfoResponse;
import ru.netcracker.backend.model.responses.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserResponse banUser(String username);

    UserResponse unbanUser(String username);

    UserResponse addMoney(String username, BigDecimal money);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(UserRequest userRequest);

    void sendMoneyToWsByUsername(String username);

    void sendMoneyToWsByUser(User user);

    ContactInfoResponse getContactInfoByLotId(Long lotId);
}
