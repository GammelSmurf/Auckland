package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.util.annotation.JsonLocalDateTime;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotResponse {
    private Long id;
    private String name;
    private String description;
    private Long minPrice;
    private Long priceIncreaseMinStep;
    private Long winPrice;
    private String pictureLink;
    private Long auctionId;
    private Boolean finished;
    private UserResponse winner;

    private boolean transferred;
    private boolean buyerAcceptConfirmation;
    private boolean sellerTransferConfirmation;

    private boolean canceled;

    @JsonLocalDateTime
    private LocalDateTime endDateTime;
}
