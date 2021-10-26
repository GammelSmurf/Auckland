package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.MessageResponse;
import ru.netcracker.backend.service.AuthService;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<String> createUser(@RequestBody AuthRequest authRequestDTO, HttpServletRequest request) {
        try {
            authService.createUser(authRequestDTO, getSiteURL(request));
            return ResponseEntity.ok("User created");
        }
        catch (Exception | EmailExistsException | UserExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}