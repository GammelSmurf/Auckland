package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.AuctionStatus;

import java.math.BigDecimal;

/**
 * This is a representation of the state of the auction after synchronization.
 */
@Getter
@Setter
@AllArgsConstructor
public class SyncResponse {

    /**
     * The time before the start of the auction
     * or before the end of the lot,
     * depending on the {@link SyncResponse#auctionStatus}.
     */
    Long timeUntil;

    /**
     * The current raffled lot in the auction
     * after synchronization.
     */
    LotResponse currentLot;

    /**
     * {@link AuctionStatus}
     * after synchronization.
     */
    AuctionStatus auctionStatus;

    /**
     * The amount of the bet
     * after synchronization.
     */
    BigDecimal currentBank;

    /**
     * Shows whether the lot has changed
     * after synchronization.
     */
    Boolean changed;
}
