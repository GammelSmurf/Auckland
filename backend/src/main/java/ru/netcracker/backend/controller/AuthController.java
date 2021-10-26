package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.MessageResponse;
import ru.netcracker.backend.service.AuthService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequestDTO) {
        return ResponseEntity.ok(authService.authenticateUser(authRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody AuthRequest authRequestDTO, HttpServletRequest request) throws UserExistsException, MessagingException, UnsupportedEncodingException, EmailExistsException {
        authService.createUser(authRequestDTO, getSiteURL(request));
        log.info("created user: {}", authRequestDTO);
        return ResponseEntity.ok("User created");
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}