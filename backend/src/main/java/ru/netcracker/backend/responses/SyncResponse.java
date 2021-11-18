package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.AuctionStatus;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SyncResponse {
    Long timeUntil;
    LotResponse currentLot;
    AuctionStatus auctionStatus;
    BigDecimal currentBank;
    Boolean changed;
    Boolean until;
}
