package ru.netcracker.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.models.domain.user.User;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.JwtResponse;
import ru.netcracker.backend.models.responses.UserResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    JwtResponse authenticateUser(AuthRequest authRequestDTO);
    void createUser(AuthRequest authRequest, String siteURL) throws MessagingException, UnsupportedEncodingException, EmailExistsException, UserExistsException;
    void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;
    boolean verify(String verificationCode);
}
