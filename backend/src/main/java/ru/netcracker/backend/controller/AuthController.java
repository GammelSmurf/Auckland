package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.requests.SignInRequest;
import ru.netcracker.backend.model.requests.SignUpRequest;
import ru.netcracker.backend.service.AuthService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> generateToken(@Valid @RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok((authService.authenticateUser(new User(
                signInRequest.getUsername(),
                signInRequest.getPassword()))));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request)
            throws UserExistsException, MessagingException, UnsupportedEncodingException,
            EmailExistsException {
        authService.createUser(new User(
                signUpRequest.getUsername(),
                signUpRequest.getPassword(),
                signUpRequest.getEmail()), getSiteURL(request));
        log.info("created user with email: {} and username: {}", signUpRequest.getEmail(), signUpRequest.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> passwordChangeRequest(@NotBlank @RequestParam("username") String username, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authService.generateNewPasswordAndSendEmail(username, getSiteURL(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/verification")
    public ResponseEntity<Void> sendVerificationEmail(
            @NotBlank @RequestParam("username") String username, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authService.sendVerificationLinkToUserEmail(username, getSiteURL(request));
        return ResponseEntity.ok().build();
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
