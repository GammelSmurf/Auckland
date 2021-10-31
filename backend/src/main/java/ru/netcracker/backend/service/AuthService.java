package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.User;
import ru.netcracker.backend.requests.AuthRequest;
import ru.netcracker.backend.responses.JwtResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    JwtResponse authenticateUser(AuthRequest authRequestDTO);

    void createUser(AuthRequest authRequest, String siteURL)
            throws MessagingException, UnsupportedEncodingException, EmailExistsException,
            UserExistsException;

    void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException;

    boolean verify(String verificationCode);
}
