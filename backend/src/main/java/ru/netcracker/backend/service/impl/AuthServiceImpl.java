package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.models.domain.user.ERole;
import ru.netcracker.backend.models.domain.user.User;
import ru.netcracker.backend.models.requests.AuthRequest;
import ru.netcracker.backend.models.responses.JwtResponse;
import ru.netcracker.backend.repository.UserRepo;
import ru.netcracker.backend.security.JwtUtil;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    final private AuthenticationProvider authenticationProvider;
    final private JwtUtil jwtUtil;
    final private UserRepo userRepo;

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
    public ResponseEntity<String> createUser(AuthRequest authRequest) {
        if(userRepo.existsByUsername(authRequest.getUsername()))
            return ResponseEntity.badRequest().body("This username is already in use!");
        else if (userRepo.existsByEmail(authRequest.getEmail()))
            return ResponseEntity.badRequest().body("This email is already in use!");
        else{
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = encoder.encode(authRequest.getPassword());
            User user = new User(authRequest.getUsername(), password, authRequest.getEmail());
            Set<ERole> roles = new HashSet<>();
            roles.add(ERole.USER);
            user.setRoles(roles);
            userRepo.save(user);

            return ResponseEntity.ok("You have signed up successfully!");
        }
    }
}
