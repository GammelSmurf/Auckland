package ru.netcracker.backend.model.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.model.entity.AuctionStatus;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.util.annotation.JsonLocalDateTime;
import ru.netcracker.backend.util.annotation.JsonLocalTime;

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
    private UserResponse creator;
    private LotResponse currentLot;

    private long usersCountLimit;
    private long subscribedUsersCount;
    private long userLikesCount;
    private Set<Lot> lots;
    private Set<UserResponse> subscribedUsers;
    private Set<CategoryResponse> categories;
    private Set<TagResponse> tags;

    @JsonLocalDateTime
    private LocalDateTime beginDateTime;
    @JsonLocalDateTime
    private LocalDateTime endDateTime;

    @JsonLocalTime
    private LocalTime lotDurationTime;

    @JsonLocalTime
    private LocalTime extraTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AuctionStatus status;
}
