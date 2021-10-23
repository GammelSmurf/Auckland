package ru.netcracker.backend.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequest {
    private String name;
    private LocalDateTime beginDate;
    private int lotDuration;
    private LocalDateTime boostTime;
    private int usersLimit;
    private long userId;
}
