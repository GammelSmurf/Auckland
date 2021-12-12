package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.auction.AuctionAlreadyContainsCategoryException;
import ru.netcracker.backend.exception.auction.NotCorrectBeginDateException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Category;

import java.time.LocalDateTime;

public class AuctionUtil {

    private AuctionUtil() {
    }

    public static void validateBeforeMakingWaiting(Auction auction) {
        if (!auction.isDraft() || auction.isWaiting() || auction.isFinished()) {
            throw new NotCorrectStatusException(auction);
        }

        if (LocalDateTime.now().isAfter(auction.getBeginDateTime())) {
            throw new NotCorrectBeginDateException();
        }
    }

    public static void validateBeforeAddingCategoryToAuction(Auction auction, Category category) {
        if (auction.getCategories().contains(category)) {
            throw new AuctionAlreadyContainsCategoryException(category);
        }
    }
}
