package ru.netcracker.backend.service;

import ru.netcracker.backend.dto.requests.AuthRequestDTO;
import ru.netcracker.backend.dto.responses.JwtResponseDTO;

public interface AuthService {
    JwtResponseDTO authenticateUser(AuthRequestDTO authRequestDTO);
}
