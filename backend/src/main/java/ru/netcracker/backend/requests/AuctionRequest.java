package ru.netcracker.backend.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.model.auction.AuctionStatus;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequest {
    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp beginDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time lotDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time boostTime;

    private int usersLimit;
    private long userId;
}
