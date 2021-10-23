package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.MessageResponse;
import ru.netcracker.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequestDTO) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(authRequestDTO));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody AuthRequest authRequestDTO) {
        try {
            return authService.createUser(authRequestDTO);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}