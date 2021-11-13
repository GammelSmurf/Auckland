package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.AuctionStatus;

public class NotCorrectStatusException extends ValidationException {
    public NotCorrectStatusException(Auction auction) {
        super(String.format("Auction is in %s status", auction.getStatus().toString().toLowerCase()));
    }
}
