package ru.netcracker.backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDto {
    private long id;
    private String name;
    private LocalDateTime beginDate;
    private int lotDuration;
    private LocalDateTime boostTime;
    private int usersLimit;
    private long userId;
}
