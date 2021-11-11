package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.requests.AuthRequest;
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
        return new ResponseEntity<>((authService.authenticateUser(authRequestDTO)), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @RequestBody AuthRequest authRequestDTO, HttpServletRequest request)
            throws UserExistsException, MessagingException, UnsupportedEncodingException,
                    EmailExistsException {
        authService.createUser(authRequestDTO, getSiteURL(request));
        log.info("created user: {}", authRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> passwordChangeRequest(
            @RequestParam("username") String username, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        authService.sentChangePasswordForm(username, getSiteURL(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
