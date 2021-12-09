package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.bet.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Bid;
import ru.netcracker.backend.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidUtil {
    private BidUtil() {
    }

    public static void validate(Auction auction, BigDecimal lotBank, User user)
            throws BankLessThanMinException, BankLessThanOldException, BankLessThanStepException,
            LotTimeExpiredException, NoCurrencyException, NotSubscribedException {
        if (user.getSubscribedAuctions().isEmpty() || !user.getSubscribedAuctions().contains(auction)) {
            throw new NotSubscribedException("The user is not subscribed to the auction");
        }

        if (!auction.isRunning()) {
            throw new NotCorrectStatusException(auction);
        }

        if (auction.getCurrentLot().getEndTime().isBefore(LocalDateTime.now())) {
            throw new LotTimeExpiredException("Lot time is expired");
        }

        if (isLess(user.getCurrency(), lotBank)) {
            throw new NoCurrencyException("User don't have enough money");
        }

        if (isLess(lotBank, auction.getCurrentLot().getMinPrice())) {
            throw new BankLessThanMinException(String.format("Bank is less than minimum: %f", auction.getCurrentLot().getMinPrice()));
        }

        if (auction.getBid() != null) {
            Bid bid = auction.getBid();
            if (isLess(lotBank, bid.getAmount())) {
                throw new BankLessThanOldException(String.format("Bank is less than the old one: %f", bid.getAmount()));
            }

            if (isLess(lotBank.subtract(bid.getAmount()), auction.getCurrentLot().getPriceStep())) {
                throw new BankLessThanStepException(String.format("Bet step is less than the minimal one: %f", auction.getCurrentLot().getPriceStep()));
            }
        }
    }

    private static boolean isLess(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }
}
