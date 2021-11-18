package ru.netcracker.backend.responses;

import lombok.*;
import ru.netcracker.backend.util.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;

    private String username;

    @JsonLocalDateTime
    private LocalDateTime dateTimeMessage;

    private String message;

}
