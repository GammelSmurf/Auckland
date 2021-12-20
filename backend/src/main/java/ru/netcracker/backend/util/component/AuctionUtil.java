package ru.netcracker.backend.util.component;

import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.auction.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Category;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.util.SecurityUtil;

import java.time.LocalDateTime;

@Component
public class AuctionUtil {
    public void validateBeforeCreating(Auction auction, AuctionRepository auctionRepository) {
        if (auctionRepository.existsByName(auction.getName())) {
            throw new AuctionNameAlreadyExistsException(auction);
        }
    }

    public void validateBeforeUpdating(Auction oldAuction, Auction newAuction, AuctionRepository auctionRepository) {
        if (!oldAuction.getName().equals(newAuction.getName()) && auctionRepository.existsByName(newAuction.getName())) {
            throw new AuctionNameAlreadyExistsException(newAuction);
        }
    }

    public void validateBeforeDeleting(Auction auction) {
        checkIfUserIsNotCreatorOfAuction(auction);
    }

    public void validateBeforeSubscribing(Auction auction) {
        checkIfUserIsCreatorOfAuction(auction);
    }

    public void validateBeforeGetting(Auction auction) {
        if (auction.isDraft() && !isAuctionCreatorInSecurityContext(auction)) {
            throw new NotCorrectStatusException(auction);
        }
    }

    public void validateBeforeMakingWaiting(Auction auction) {
        checkIfUserIsNotCreatorOfAuction(auction);
        if (!auction.isDraft() || auction.isWaiting() || auction.isFinished()) {
            throw new NotCorrectStatusException(auction);
        }

        if (LocalDateTime.now().isAfter(auction.getBeginDateTime())) {
            throw new NotCorrectBeginDateException();
        }
    }

    private void checkIfUserIsCreatorOfAuction(Auction auction) {
        if (isAuctionCreatorInSecurityContext(auction)) {
            throw new AuctionIsOwnByUserException(auction);
        }
    }

    private void checkIfUserIsNotCreatorOfAuction(Auction auction) {
        if (!isAuctionCreatorInSecurityContext(auction)) {
            throw new AuctionIsNotOwnByUserException(auction);
        }
    }

    public void validateBeforeAddingCategoryToAuction(Auction auction, Category category) {
        if (auction.getCategories().contains(category)) {
            throw new AuctionAlreadyContainsCategoryException(category);
        }
    }

    private boolean isAuctionCreatorInSecurityContext(Auction auction) {
        return auction.getCreator().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }

    public void validateBeforeLike(Auction auction, User user) {
        checkIfUserSubscribed(auction, user);
        if (auction.getUsersWhoLiked().contains(user)) {
            throw new AlreadyLikedException(auction, user);
        }
    }

    public void validateBeforeDislike(Auction auction, User user) {
        checkIfUserSubscribed(auction, user);
        if (!auction.getUsersWhoLiked().contains(user)) {
            throw new NotLikedException(auction, user);
        }
    }

    private void checkIfUserSubscribed(Auction auction, User user) {
        if (!auction.getSubscribedUsers().contains(user)) {
            throw new NotSubscribedException();
        }
    }
}
