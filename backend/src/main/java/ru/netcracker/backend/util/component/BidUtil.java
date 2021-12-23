package ru.netcracker.backend.util.component;

import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.bid.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BidUtil {
    public void validate(Auction auction, BigDecimal amount, User user)
            throws BankLessThanMinException, BankLessThanStepException,
            LotTimeExpiredException, NoCurrencyException, NotSubscribedException {
        if (user.getSubscribedAuctions().isEmpty() || !user.getSubscribedAuctions().contains(auction)) {
            throw new NotSubscribedException();
        }

        if (!auction.isRunning()) {
            throw new NotCorrectStatusException(auction);
        }

        if (auction.getCurrentLot().getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new LotTimeExpiredException();
        }

        if (isNegative(amount) || isBlank(amount)) {
            throw new AmountIsNegativeOrBlankException();
        }

        if (isLess(user.getMoney(), amount)) {
            throw new NoCurrencyException();
        }

        if (isLess(amount, auction.getCurrentLot().getMinPrice())) {
            throw new BankLessThanMinException(auction);
        }

        if (!isNoBid(auction) && isLess(amount, auction.getCurrentBid().getAmount())) {
            throw new BankLessThanOldException(auction);
        }

        if (!isNoBid(auction) && isLess(amount.subtract(auction.getCurrentBid().getAmount()), auction.getCurrentLot().getPriceIncreaseMinStep())) {
            throw new BankLessThanStepException(auction);
        }
    }

    private boolean isNoBid(Auction auction) {
        return auction.getCurrentBid() == null;
    }

    private boolean isLess(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public boolean isNegative(BigDecimal amount) {
        return amount.signum() == -1;
    }

    public boolean isBlank(BigDecimal amount) {
        return (amount == null || amount.signum() == 0);
    }
}
