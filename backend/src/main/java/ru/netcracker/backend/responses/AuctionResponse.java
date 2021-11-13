package ru.netcracker.backend.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.AuctionStatus;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionResponse {
    private long id;
    private String name;
    private String description;

    private int usersLimit;
    private int usersCount;
    private int userLikes;
    private long userId;
    private Set<Lot> lots;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime lotDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime boostTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AuctionStatus status;
}
