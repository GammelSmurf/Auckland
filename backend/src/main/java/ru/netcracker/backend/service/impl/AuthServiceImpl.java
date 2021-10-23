package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.dto.requests.AuthRequestDTO;
import ru.netcracker.backend.dto.responses.JwtResponseDTO;
import ru.netcracker.backend.security.JwtUtil;
import ru.netcracker.backend.security.MyUserDetails;
import ru.netcracker.backend.service.AuthService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    final private AuthenticationProvider authenticationProvider;
    final private JwtUtil jwtUtil;

    @Override
    public JwtResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String jwt = jwtUtil.generateJwtToken(userDetails);
        return new JwtResponseDTO(
                userDetails.getId(),
                jwt, userDetails.getUsername(), roles);
    }
}
