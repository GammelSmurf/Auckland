package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;

public class BankLessThanStepException extends ValidationException {
    public BankLessThanStepException(Auction auction) {
        super(String.format("Bet step is less than the minimal one: %f", auction.getCurrentLot().getPriceIncreaseMinStep()));
    }
}
