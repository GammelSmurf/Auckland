package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.bet.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BetUtil {
    public static final String BET_NOT_FOUND_TEMPLATE = "Bet with auction id: %d was not found";
    public static final String RETURN_TIME_MSG_PATTERN = "dd HH-mm-ss";
    public static final String TIME_ZONE = "Europe/Moscow";
    public static final String RETURN_NULL_DATA_MSG = "00 00-00-00";

    private BetUtil() {
    }

    public static void validate(Auction auction, BigDecimal lotBank, User user)
            throws BankLessThanMinException, BankLessThanOldException, BankLessThanStepException,
            LotTimeExpiredException, NoCurrencyException, NotSubscribedException {

        if (user.getSubscribes().isEmpty() || !user.getSubscribes().contains(auction)) {
            throw new NotSubscribedException("The user is not subscribed to the auction");
        }

        if (auction.getCurrentLot().getEndTime().isBefore(LocalDateTime.now())) {
            throw new LotTimeExpiredException("Lot time is expired");
        }

        if (isLess(
                user.getCurrency(),
                lotBank)) {
            throw new NoCurrencyException("User don't have enough money");
        }

        if (isLess(
                lotBank,
                auction.getCurrentLot().getMinBank())) {
            throw new BankLessThanMinException(String.format("Bank is less than minimum: %f", auction.getCurrentLot().getMinBank()));
        }

        if (auction.getBet() != null) {
            Bet bet = auction.getBet();
            if (isLess(
                    lotBank,
                    bet.getCurrentBank())) {
                throw new BankLessThanOldException(String.format("Bank is less than the old one: %f", bet.getCurrentBank()));
            }

            if (isLess(
                    lotBank.subtract(bet.getCurrentBank()),
                    auction.getCurrentLot().getStep())) {
                throw new BankLessThanStepException(String.format("Bet step is less than the minimal one: %f", auction.getCurrentLot().getStep()));
            }
        }
    }

    private static boolean isLess(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == -1;
    }
}
