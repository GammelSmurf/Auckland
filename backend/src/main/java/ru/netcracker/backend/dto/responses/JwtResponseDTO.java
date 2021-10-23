package ru.netcracker.backend.dto.responses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponseDTO {
    private Long id;
    private String token;
    private final String type = "Bearer";
    private String username;
    private List<String> roles;
}