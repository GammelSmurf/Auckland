package ru.netcracker.backend.service.impl;

import lombok.*;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.ERole;
import ru.netcracker.backend.model.user.User;
import ru.netcracker.backend.requests.AuthRequest;
import ru.netcracker.backend.responses.JwtResponse;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.security.JwtUtil;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;
import ru.netcracker.backend.util.MimeMessageBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${Auckland.mail.senderName}")
    private String senderName;

    private final AuthenticationProvider authenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder encoder;

    @Override
    public JwtResponse authenticateUser(AuthRequest authRequestDTO) {
        Authentication authentication =
                authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        List<String> roles =
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

        String jwt = jwtUtil.generateJwtToken(userDetails);
        return new JwtResponse(userDetails.getId(), jwt, userDetails.getUsername(), roles);
    }

    @Override
    public void createUser(AuthRequest authRequest, String siteURL)
            throws MessagingException, UnsupportedEncodingException, EmailExistsException,
                    UserExistsException {
        if (userRepository.existsByEmail(authRequest.getEmail()))
            throw new EmailExistsException(
                    "There is an account with that email address: " + authRequest.getEmail());
        if (userRepository.existsByEmail(authRequest.getEmail()))
            throw new UserExistsException(
                    "There is an account with that username: " + authRequest.getUsername());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(authRequest.getPassword());
        User user = new User(authRequest.getUsername(), password, authRequest.getEmail());
        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.USER);
        user.setRoles(roles);
        user.setCurrency(0);
        User userToVerify = userRepository.save(user);

        sendVerificationEmail(userToVerify, siteURL);
    }

    @Override
    public void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message =
                new MimeMessageBuilder(javaMailSender)
                        .setFromAddress(fromAddress)
                        .setSenderName(senderName)
                        .setTo(user)
                        .setSubject("Please verify your registration")
                        .setMethodURL(
                                siteURL
                                        + "/verify?code="
                                        + user.getVerificationCode()
                                        + "&username="
                                        + user.getUsername())
                        .setContent(
                                "Dear [[name]],<br>"
                                        + "Please click the link below to verify your registration:<br>"
                                        + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                                        + "Thank you,<br>"
                                        + "Auckland Team.")
                        .build();

        javaMailSender.send(message);
    }

    @Override
    public boolean verify(String verificationCode, String username) {
        User user = userRepository.findUserByUsername(username);

        if (user == null
                || !Objects.equals(user.getVerificationCode(), verificationCode)
                || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

    @Override
    public void sentChangePasswordForm(String username, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            return;
        }
        user.setRestoreCode(RandomString.make(64));
        userRepository.save(user);

        MimeMessage message =
                new MimeMessageBuilder(javaMailSender)
                        .setFromAddress(fromAddress)
                        .setSenderName(senderName)
                        .setTo(user)
                        .setSubject("Password recover")
                        .setMethodURL(
                                siteURL
                                        + "/restore?code="
                                        + user.getRestoreCode()
                                        + "&username="
                                        + user.getUsername())
                        .setContent(
                                "Dear [[name]],<br>"
                                        + "Please click the link below to restore your password:<br>"
                                        + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                                        + "Thank you,<br>"
                                        + "Auckland Team.")
                        .build();

        javaMailSender.send(message);
    }

    @Override
    public void generateNewPassword(String restoreCode, String username)
            throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findUserByUsername(username);

        if (user == null || !Objects.equals(user.getRestoreCode(), restoreCode)) {
            return;
        } else {
            String password = RandomString.make(8);
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            MimeMessage message =
                    new MimeMessageBuilder(javaMailSender)
                            .setFromAddress(fromAddress)
                            .setSenderName(senderName)
                            .setTo(user)
                            .setSubject("Password recover")
                            .setContent(
                                    "Dear [[name]],<br>"
                                            + "Here is your new password:<br>"
                                            + "<h3>"
                                            + password
                                            + "</h3>"
                                            + "Thank you,<br>"
                                            + "Auckland Team.")
                            .build();

            javaMailSender.send(message);
        }
    }
}
