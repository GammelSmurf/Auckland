package ru.netcracker.backend.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionLogResponse {
    private String logMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logTime;
}
