package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.User;

public class NotLikedException extends ValidationException {
    public NotLikedException(Auction auction, User user) {
        super(String.format("The user with name: %s didn't like auction with name: %s", user.getUsername(), auction.getName()));
    }
}
