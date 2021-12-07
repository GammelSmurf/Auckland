package ru.netcracker.backend.util;

import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Lot;

import java.util.Optional;

public class AuctionUtil {
    public static final String AUCTION_NOT_FOUND_TEMPLATE = "Auction with id: %d was not found";
    public static final String CATEGORY_NOT_FOUND_TEMPLATE = "Category with id: %d was not found";

    private AuctionUtil() {
    }

    public static Optional<Lot> getAnotherLot(Auction auction) {
        return auction.getLots().stream().filter(lot -> !lot.isFinished()).findFirst();
    }
}
