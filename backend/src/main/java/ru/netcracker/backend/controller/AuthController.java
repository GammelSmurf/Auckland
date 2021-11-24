package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.requests.AuthRequest;
import ru.netcracker.backend.service.AuthService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>((authService.authenticateUser(
                new User(
                        authRequest.getUsername(),
                        authRequest.getPassword(),
                        authRequest.getEmail()))),
                HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @RequestBody AuthRequest authRequest, HttpServletRequest request)
            throws UserExistsException, MessagingException, UnsupportedEncodingException,
            EmailExistsException {
        authService.createUser(new User(
                authRequest.getUsername(),
                authRequest.getPassword(),
                authRequest.getEmail()), getSiteURL(request));
        log.info("created user: {}", authRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> passwordChangeRequest(
            @RequestParam("username") String username, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authService.sendChangePasswordRequestToUserEmail(username, getSiteURL(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/send/verification")
    public ResponseEntity<Void> sendVerificationEmail(
            @RequestParam("username") String username, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authService.sendVerificationLinkToUserEmail(username, getSiteURL(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
