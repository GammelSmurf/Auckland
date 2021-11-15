package ru.netcracker.backend.service.impl;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.Role;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.responses.JwtResponse;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.security.JwtUtil;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;
import ru.netcracker.backend.util.MimeMessageBuilder;
import ru.netcracker.backend.util.UserUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final AuthenticationProvider authenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder encoder;
    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${Auckland.mail.senderName}")
    private String senderName;

    private final String HELLO_MSG_TEMPLATE = "Dear [[name]],<br>";
    private final String BYE_MSG_TEMPLATE = "Thank you,<br> %s.";

    @Autowired
    public AuthServiceImpl(
            AuthenticationProvider authenticationProvider,
            JwtUtil jwtUtil, UserRepository userRepository,
            JavaMailSender javaMailSender,
            BCryptPasswordEncoder encoder) {
        this.authenticationProvider = authenticationProvider;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
        this.encoder = encoder;
    }

    @Override
    public JwtResponse authenticateUser(User user) {
        Authentication authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String jwt = jwtUtil.generateJwtToken(userDetails);
        return new JwtResponse(userDetails.getId(), jwt, userDetails.getUsername(), roles);
    }

    @Override
    public void createUser(User user, String siteURL)
            throws EmailExistsException, UserExistsException, MessagingException, UnsupportedEncodingException {
        UserUtil.validate(user, userRepository);
        user.setPassword(encoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        sendVerificationEmail(userRepository.save(user), siteURL);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendVerificationEmail(String username, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            sendVerificationEmail(userOptional.get(), siteURL);
        }
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = buildMimeMessage(user)
                .setSubject("Please verify your registration")
                .setMethodURL(String.format(
                        "%s/verify?username=%s&code=%s",
                        siteURL, user.getUsername(), user.getVerificationCode()))
                .setContent(generateContent(
                        "Please click the link below to verify your registration:<br>"
                                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"))
                .build();
        javaMailSender.send(message);
    }

    @Override
    public boolean verify(String username, String verificationCode) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() &&
                userOptional.get().getVerificationCode().equals(verificationCode) &&
                !userOptional.get().isEnabled()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void sendChangePasswordRequestToUserEmail(String username, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRestoreCode(RandomString.make(64));
            userRepository.save(user);

            MimeMessage message = buildMimeMessage(user)
                    .setSubject("Password recover")
                    .setMethodURL(String.format(
                            "%s/restore?username=%s&code=%s",
                            siteURL, user.getUsername(), user.getRestoreCode()))
                    .setContent(generateContent(
                            "Please click the link below to restore your password:<br>"
                                    + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"))
                    .build();
            javaMailSender.send(message);
        }
    }

    @Override
    public void generateNewPassword(String username, String restoreCode)
            throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() &&
                userOptional.get().getRestoreCode().equals(restoreCode)) {
            User user = userOptional.get();
            String password = RandomString.make(8);
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            MimeMessage message = buildMimeMessage(user)
                    .setSubject("Password recover")
                    .setContent(generateContent(
                            "Here is your new password:<br><h3>"
                                    + password
                                    + "</h3>"))
                    .build();
            javaMailSender.send(message);
        }
    }

    private String generateContent(String content) {
        return HELLO_MSG_TEMPLATE + content + String.format(BYE_MSG_TEMPLATE, senderName);
    }

    private MimeMessageBuilder buildMimeMessage(User to) {
        return new MimeMessageBuilder(javaMailSender)
                .setFromAddress(fromAddress)
                .setSenderName(senderName)
                .setTo(to);
    }
}
