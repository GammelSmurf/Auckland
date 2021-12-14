package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.util.SecurityUtil;

public class AuctionIsOwnByUserException extends ValidationException {
    public AuctionIsOwnByUserException(Auction auction) {
        super(String.format("User: %s is trying to access to auction: %s that belong to", SecurityUtil.getUsernameFromSecurityCtx(), auction.getName()));
    }
}
