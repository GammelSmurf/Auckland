package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.auction.*;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Category;
import ru.netcracker.backend.repository.AuctionRepository;

import java.time.LocalDateTime;

public class AuctionUtil {

    private AuctionUtil() {
    }

    public static void validateBeforeCreatingOrUpdating(Auction auction, AuctionRepository auctionRepository) {
        if (auctionRepository.existsByName(auction.getName())) {
            throw new AuctionNameAlreadyExistsException(auction);
        }
    }

    public static void validateBeforeDeleting(Auction auction) {
        checkIfUserIsCreatorOfAuction(auction);
    }

    public static void validateBeforeSubscribing(Auction auction) {
        checkIfUserIsCreatorOfAuction(auction);
    }

    public static void validateBeforeGetting(Auction auction) {
        if (auction.isDraft() && !isAuctionCreatorInSecurityContext(auction)) {
            throw new NotCorrectStatusException(auction);
        }
    }

    public static void validateBeforeMakingWaiting(Auction auction) {
        checkIfUserIsCreatorOfAuction(auction);
        if (!auction.isDraft() || auction.isWaiting() || auction.isFinished()) {
            throw new NotCorrectStatusException(auction);
        }

        if (LocalDateTime.now().isAfter(auction.getBeginDateTime())) {
            throw new NotCorrectBeginDateException();
        }
    }

    private static void checkIfUserIsCreatorOfAuction(Auction auction) {
        if (isAuctionCreatorInSecurityContext(auction)) {
            throw new AuctionIsOwnByUserException(auction);
        }
    }

    public static void validateBeforeAddingCategoryToAuction(Auction auction, Category category) {
        if (auction.getCategories().contains(category)) {
            throw new AuctionAlreadyContainsCategoryException(category);
        }
    }

    private static boolean isAuctionCreatorInSecurityContext(Auction auction) {
        return auction.getCreator().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }
}
