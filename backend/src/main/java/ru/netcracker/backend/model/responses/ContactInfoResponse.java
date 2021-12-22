package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoResponse {
    private String username;
    private String email;
    private String firstName;
    private String secondName;
    private String about;
}
