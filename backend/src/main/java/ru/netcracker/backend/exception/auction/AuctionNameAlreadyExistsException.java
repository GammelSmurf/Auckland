package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;

public class AuctionNameAlreadyExistsException extends ValidationException {
    public AuctionNameAlreadyExistsException(Auction auction) {
        super(String.format("Auction with the name: %s already exists", auction.getName()));
    }
}
