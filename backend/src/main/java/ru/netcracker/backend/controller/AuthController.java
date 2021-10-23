package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.dto.requests.AuthRequestDTO;
import ru.netcracker.backend.dto.responses.MessageResponseDTO;
import ru.netcracker.backend.service.AuthService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(authRequestDTO));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(e.getMessage()));
        }
    }

}