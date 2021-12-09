package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;

public class AuctionNotFoundException extends ValidationException {
    public AuctionNotFoundException(Long auctionId) {
        super(String.format("Auction with id: %d was not found", auctionId));
    }
}
