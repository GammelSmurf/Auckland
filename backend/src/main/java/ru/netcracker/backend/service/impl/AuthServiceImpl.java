package ru.netcracker.backend.service.impl;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.entity.UserRole;
import ru.netcracker.backend.model.responses.JwtResponse;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;
import ru.netcracker.backend.util.component.JwtUtil;
import ru.netcracker.backend.util.component.UserUtil;
import ru.netcracker.backend.util.component.email.EmailSender;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final AuthenticationProvider authenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailSender emailSender;
    private final UserUtil userUtil;

    @Autowired
    public AuthServiceImpl(
            AuthenticationProvider authenticationProvider,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            BCryptPasswordEncoder encoder,
            EmailSender emailSender,
            UserUtil userUtil) {
        this.authenticationProvider = authenticationProvider;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.encoder = encoder;
        this.userUtil = userUtil;
    }

    @Override
    @Transactional
    public JwtResponse authenticateUser(User user) {
        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(
                userDetails.getId(),
                jwtUtil.generateJwtToken(userDetails),
                userDetails.getUsername(),
                userDetails.getUser().getMoney(),
                roles);
    }

    @Override
    @Transactional
    public void createUser(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        userUtil.validateBeforeSigningUp(user, userRepository);
        user.setPassword(encoder.encode(user.getPassword()));
        user.getUserRoles().add(UserRole.USER);
        emailSender.createAndSendVerificationEmail(userRepository.save(user), siteURL);
    }

    @Override
    public void sendVerificationLinkToUserEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            emailSender.createAndSendVerificationEmail(userOptional.get(), siteURL);
        }
    }

    @Override
    @Transactional
    public boolean verify(String username, String verificationCode) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && isUserValidToVerify(userOptional.get(), verificationCode)) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    private boolean isUserValidToVerify(User user, String verificationCode) {
        return user.getVerificationCode().equals(verificationCode) && !user.isEnabled();
    }

    @Override
    @Transactional
    public void generateNewPasswordAndSendEmail(String username, String siteURL) throws MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRestoreCode(RandomString.make(64));
            userRepository.save(user);
            emailSender.createAndSendGeneratedPasswordEmail(user, siteURL);
        }
    }
}
