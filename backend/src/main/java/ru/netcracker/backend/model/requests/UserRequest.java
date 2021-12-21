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
public class UserRequest {
    private String username;

    @Size(min=2,max=250)
    private String firstName;

    @Size(min=2,max=250)
    private String secondName;

    @Size(min=3,max=250)
    private String about;
}
