package ru.netcracker.backend.service;

import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(AuthRequest authRequestDTO);
}
