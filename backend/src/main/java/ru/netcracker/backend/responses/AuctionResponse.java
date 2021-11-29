package ru.netcracker.backend.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.model.AuctionStatus;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.util.JsonLocalDateTime;
import ru.netcracker.backend.util.JsonLocalTime;

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
    private Long creatorId;

    private int usersLimit;
    private int usersCount;
    private int userLikes;
    private Set<Lot> lots;
    private Set<UserResponse> subscribers;

    @JsonLocalDateTime
    private LocalDateTime beginDate;
    @JsonLocalDateTime
    private LocalDateTime endDate;

    @JsonLocalTime
    private LocalTime lotDuration;

    @JsonLocalTime
    private LocalTime boostTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AuctionStatus status;
}
