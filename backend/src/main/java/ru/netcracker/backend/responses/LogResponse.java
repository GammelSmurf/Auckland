package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.util.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogResponse {
    private Long id;
    private String logMessage;

    @JsonLocalDateTime
    private LocalDateTime logTime;
}
