package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;

public class BankLessThanMinException extends ValidationException {
    public BankLessThanMinException(Auction auction) {
        super(String.format("Bank is less than minimum: %f", auction.getCurrentLot().getMinPrice()));
    }
}
