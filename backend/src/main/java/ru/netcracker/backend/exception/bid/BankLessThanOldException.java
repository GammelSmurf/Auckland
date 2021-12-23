package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Bid;

public class BankLessThanOldException extends ValidationException {
    public BankLessThanOldException(Auction auction) {
        super(String.format("Bank is less than the old one: %f", auction.getCurrentBid().getAmount()));
    }
}
