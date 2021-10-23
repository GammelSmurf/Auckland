package ru.netcracker.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(AuthRequest authRequestDTO);
    ResponseEntity<String> createUser(AuthRequest authRequest);
}
