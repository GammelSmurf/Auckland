package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.bet.*;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.SyncResponse;

import java.math.BigDecimal;

public interface BetService {
    BetResponse makeBet(String username, Long auctionId, BigDecimal lotBank) throws ValidationException;

    SyncResponse sync(Long auctionId) throws NotCorrectStatusException;
}
