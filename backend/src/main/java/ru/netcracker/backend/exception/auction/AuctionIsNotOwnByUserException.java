package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.util.SecurityUtil;

public class AuctionIsNotOwnByUserException extends ValidationException {
    public AuctionIsNotOwnByUserException(Auction auction) {
        super(String.format("User: %s is trying to access to auction: %s that doesn't belong to", SecurityUtil.getUsernameFromSecurityCtx(), auction.getName()));
    }
}
