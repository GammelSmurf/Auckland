package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.User;

public class AlreadyLikedException extends ValidationException {
    public AlreadyLikedException(Auction auction, User user) {
        super(String.format("The user with name: %s has already liked auction with name: %s", user.getUsername(), auction.getName()));
    }
}
