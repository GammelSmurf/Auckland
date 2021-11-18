package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.util.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String senderUsername;
    private Long auctionId;

    @JsonLocalDateTime
    private LocalDateTime dateTimeMessage;

    private String message;
}
