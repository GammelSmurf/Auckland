package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.entity.AuctionStatus;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SyncResponse {
    private Long secondsUntil;
    private LotResponse currentLot;
    private AuctionStatus auctionStatus;
    private BigDecimal amount;
    private Boolean changed;
}
