package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message="Username should not be blank")
    @Size(min=3,max=250)
    private String username;

    @NotBlank(message="Password should not be blank")
    @Size(min=3,max=255)
    private String password;

    @NotBlank(message="Email should not be blank")
    @Size(min=3,max=255)
    @Pattern(regexp = "^(.+)@(\\\\S+)$")
    private String email;
}
