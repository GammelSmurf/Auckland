package ru.netcracker.backend.model.responses;

import lombok.*;
import ru.netcracker.backend.util.annotation.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;

    private String username;

    @JsonLocalDateTime
    private LocalDateTime dateTime;

    private String message;

}
