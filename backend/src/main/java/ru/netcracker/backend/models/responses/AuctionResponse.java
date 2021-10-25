package ru.netcracker.backend.models.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionResponse {
    private long id;
    private String name;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp beginDate;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time lotDuration;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time boostTime;
    private int usersLimit;
    private int usersCount;
    private int userLikes;
    private long userId;

}
