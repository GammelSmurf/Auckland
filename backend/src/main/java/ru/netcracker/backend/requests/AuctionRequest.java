package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.util.JsonLocalDateTime;
import ru.netcracker.backend.util.JsonLocalTime;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequest {
  private String name;
  private String description;

  private Integer usersLimit;
  private String creatorUsername;

  @JsonLocalDateTime
  private LocalDateTime beginDate;

  @JsonLocalTime
  private LocalTime lotDuration;

  @JsonLocalTime
  private LocalTime boostTime;
}
