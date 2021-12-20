package ru.netcracker.backend.util.component;

import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.bet.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Bid;
import ru.netcracker.backend.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BidUtil {
    public void validate(Auction auction, BigDecimal amount, User user)
            throws BankLessThanMinException, BankLessThanOldException, BankLessThanStepException,
            LotTimeExpiredException, NoCurrencyException, NotSubscribedException {
        if (user.getSubscribedAuctions().isEmpty() || !user.getSubscribedAuctions().contains(auction)) {
            throw new NotSubscribedException();
        }

        if (!auction.isRunning()) {
            throw new NotCorrectStatusException(auction);
        }

        if (auction.getCurrentLot().getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new LotTimeExpiredException("Lot time is expired");
        }

        if (isLess(user.getMoney(), amount)) {
            throw new NoCurrencyException("User don't have enough money");
        }

        if (isLess(amount, auction.getCurrentLot().getMinPrice())) {
            throw new BankLessThanMinException(String.format("Bank is less than minimum: %f", auction.getCurrentLot().getMinPrice()));
        }

        if (auction.getCurrentBid() != null) {
            Bid bid = auction.getCurrentBid();
            if (isLess(amount, bid.getAmount())) {
                throw new BankLessThanOldException(String.format("Bank is less than the old one: %f", bid.getAmount()));
            }

            if (isLess(amount.subtract(bid.getAmount()), auction.getCurrentLot().getPriceIncreaseMinStep())) {
                throw new BankLessThanStepException(String.format("Bet step is less than the minimal one: %f", auction.getCurrentLot().getPriceIncreaseMinStep()));
            }
        }
    }

    private boolean isLess(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }
}
