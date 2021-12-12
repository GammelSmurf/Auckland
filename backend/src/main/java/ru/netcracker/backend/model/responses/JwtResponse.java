package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
    private final String type = "Bearer";
    private String username;
    private List<String> roles;
}
