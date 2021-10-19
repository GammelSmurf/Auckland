package ru.netcracker.backend.service;

import ru.netcracker.backend.dto.requests.AuthRequest;
import ru.netcracker.backend.dto.responses.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(AuthRequest authRequest);
}
