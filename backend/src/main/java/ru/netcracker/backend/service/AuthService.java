package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.responses.JwtResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    JwtResponse authenticateUser(User user);

    void createUser(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;

    void sendVerificationLinkToUserEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verify(String username, String verificationCode);

    void generateNewPasswordAndSendEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException;
}
