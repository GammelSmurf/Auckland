package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String secondName;
    private String about;
    private Boolean isBanned;
    private Boolean enabled;
    private BigDecimal currency;
}
