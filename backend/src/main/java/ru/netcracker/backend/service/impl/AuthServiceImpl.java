package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.exception.EmailExistsException;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.models.domain.user.ERole;
import ru.netcracker.backend.models.domain.user.User;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.JwtResponse;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.security.JwtUtil;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${spring.mail.username}")
    private String localEmail;

    final private AuthenticationProvider authenticationProvider;
    final private JwtUtil jwtUtil;
    final private UserRepository userRepo;
    final private JavaMailSender javaMailSender;

    @Override
    public JwtResponse authenticateUser(AuthRequest authRequestDTO) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String jwt = jwtUtil.generateJwtToken(userDetails);
        return new JwtResponse(
                userDetails.getId(),
                jwt, userDetails.getUsername(), roles);
    }

    @Override
    public void createUser(AuthRequest authRequest, String siteURL) throws MessagingException, UnsupportedEncodingException, EmailExistsException, UserExistsException {
        if (userRepo.existsByEmail(authRequest.getEmail()))
            throw new EmailExistsException("There is an account with that email address: " + authRequest.getEmail());
        else if (userRepo.existsByEmail(authRequest.getEmail()))
            throw new UserExistsException("There is an account with that username: " + authRequest.getUsername());
        else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = encoder.encode(authRequest.getPassword());
            User user = new User(authRequest.getUsername(), password, authRequest.getEmail());
            Set<ERole> roles = new HashSet<>();
            roles.add(ERole.USER);
            user.setRoles(roles);
            User userToVerify = userRepo.save(user);

            sendVerificationEmail(userToVerify, siteURL);
        }
    }

    @Override
    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = localEmail;
        String senderName = "Auckland team";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    public boolean verify(String verificationCode) {
        User user = userRepo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepo.save(user);

            return true;
        }

    }
}
