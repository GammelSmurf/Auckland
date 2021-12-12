package ru.netcracker.backend.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.netcracker.backend.util.annotation.JsonLocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotBlank(message="Sender's name should not be blank")
    @Size(min=3,max=255)
    private String senderUsername;
    private Long auctionId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonLocalDateTime
    private LocalDateTime dateTime;

    @NotBlank(message="Message should not be blank")
    @Size(min=1,max=255)
    private String message;
}
