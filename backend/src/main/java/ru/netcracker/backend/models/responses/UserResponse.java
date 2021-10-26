package ru.netcracker.backend.models.responses;

import ru.netcracker.backend.models.domain.user.EStatus;

import java.util.List;

public class UserResponse {
    private String username;
    private String password;
    private String email;
    private String name;
    private String secondName;
    private String about;
    private EStatus status;
    private Boolean isBanned;
    private Boolean enabled;
}
