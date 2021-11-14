package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.util.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotResponse {
    private Long id;
    private String name;
    private String description;
    private Long minBank;
    private Long step;
    private String picture;
    private Long auctionId;
    private Boolean finished;

    @JsonLocalDateTime
    private LocalDateTime endTime;
}
