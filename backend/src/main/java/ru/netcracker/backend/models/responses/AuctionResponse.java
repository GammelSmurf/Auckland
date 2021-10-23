package ru.netcracker.backend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionResponse {
    private String name;
    private LocalDateTime beginDate;
    private int lotDuration;
    private LocalDateTime boostTime;
    private int usersLimit;
    private int usersCount;
    private int userLikes;
    private long userId;

}
