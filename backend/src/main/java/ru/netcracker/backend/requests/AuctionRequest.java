package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.netcracker.backend.util.JsonLocalDateTime;
import ru.netcracker.backend.util.JsonLocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequest {
  @NotBlank(message="Auction's name should not be blank")
  @Size(min=3,max=255)
  private String name;
  @NotBlank(message="Auction's description should not be blank")
  @Size(min=3,max=10000)
  private String description;

  @Min(5)
  private Integer usersLimit;

  @NotBlank(message="Creator name should not be blank")
  @Size(min=3,max=255)
  private String creatorUsername;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonLocalDateTime
  private LocalDateTime beginDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  @JsonLocalTime
  private LocalTime lotDuration;

  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  @JsonLocalTime
  private LocalTime boostTime;
}
