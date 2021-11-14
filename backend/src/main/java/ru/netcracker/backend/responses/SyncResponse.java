package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.AuctionStatus;

@Getter
@Setter
@AllArgsConstructor
public class SyncResponse {
    String timeUntil;
    LotResponse currentLot;
    AuctionStatus auctionStatus;
    Boolean changed;
    Boolean until;
}
