package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.auction.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Category;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.repository.AuctionRepository;

import java.time.LocalDateTime;

public class AuctionUtil {

    private AuctionUtil() {
    }

    public static void validateBeforeCreating(Auction auction, AuctionRepository auctionRepository) {
        if (auctionRepository.existsByName(auction.getName())) {
            throw new AuctionNameAlreadyExistsException(auction);
        }
    }

    public static void validateBeforeUpdating(Auction oldAuction, Auction newAuction, AuctionRepository auctionRepository) {
        if (!oldAuction.getName().equals(newAuction.getName()) && auctionRepository.existsByName(newAuction.getName())) {
            throw new AuctionNameAlreadyExistsException(newAuction);
        }
    }

    public static void validateBeforeDeleting(Auction auction) {
        if (!isAuctionCreatorInSecurityContext(auction)) {
            throw new AuctionIsNotOwnByUserException(auction);
        }
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
        if (!isAuctionCreatorInSecurityContext(auction)) {
            throw new AuctionIsNotOwnByUserException(auction);
        }

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

    public static void validateBeforeLike(Auction auction, User user) {
        checkIfUserSubscribed(auction, user);
        if (auction.getUsersWhoLiked().contains(user)) {
            throw new AlreadyLikedException(auction, user);
        }
    }

    public static void validateBeforeDislike(Auction auction, User user) {
        checkIfUserSubscribed(auction, user);
        if (!auction.getUsersWhoLiked().contains(user)) {
            throw new NotLikedException(auction, user);
        }
    }

    private static void checkIfUserSubscribed(Auction auction, User user) {
        if (!auction.getSubscribedUsers().contains(user)) {
            throw new NotSubscribedException();
        }
    }
}
