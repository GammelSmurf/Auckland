package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.responses.JwtResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    JwtResponse authenticateUser(User user);

    void createUser(User user, String siteURL)
            throws EmailExistsException, UserExistsException, MessagingException, UnsupportedEncodingException;

    void sendVerificationEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verify(String username, String verificationCode);

    void sendChangePasswordRequestToUserEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException;

    void generateNewPassword(String username, String restoreCode) throws MessagingException, UnsupportedEncodingException;
}
