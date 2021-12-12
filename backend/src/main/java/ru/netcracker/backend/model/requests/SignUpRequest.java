package ru.netcracker.backend.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message="Username should not be blank")
    @Size(min=3,max=250)
    private String username;

    @NotBlank(message="Password should not be blank")
    @Size(min=3,max=255)
    private String password;

    @NotBlank(message = "Email should not be blank")
    @Size(min = 3, max = 255)
    @Pattern(regexp = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$")
    private String email;
}
