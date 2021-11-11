package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.bet.BankLessThanMinException;
import ru.netcracker.backend.exception.bet.BankLessThanOldException;
import ru.netcracker.backend.exception.bet.BankLessThanStepException;
import ru.netcracker.backend.model.Bet;

public interface BetService {
    Bet createBet(Bet bet);

    Bet getBet(Long auctionId);

    Bet makeBet(long id, Bet bet) throws BankLessThanStepException, BankLessThanMinException, BankLessThanOldException;

    String getRemainingTime(long auctionId);
}
