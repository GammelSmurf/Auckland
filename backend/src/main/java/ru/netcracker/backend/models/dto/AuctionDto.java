package ru.netcracker.backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDto {
    private String name;
    private LocalDateTime beginDate;
    private Integer lotDuration;
    private LocalDateTime boostTime;
    private Integer usersLimit;
    private Long userId;
}
