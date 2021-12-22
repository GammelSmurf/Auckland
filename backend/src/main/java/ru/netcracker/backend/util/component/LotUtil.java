package ru.netcracker.backend.util.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.auction.AuctionIsNotOwnByUserException;
import ru.netcracker.backend.exception.auction.AuctionNotFoundException;
import ru.netcracker.backend.exception.lot.LotNameAlreadyExistsException;
import ru.netcracker.backend.exception.lot.LotNotWonException;
import ru.netcracker.backend.exception.lot.UserIsNotLotWinnerException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.LotRepository;

@Component
public class LotUtil {
    private final LotRepository lotRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public LotUtil(LotRepository lotRepository, AuctionRepository auctionRepository) {
        this.lotRepository = lotRepository;
        this.auctionRepository = auctionRepository;
    }

    public void validateBeforeConfirmationLotTransfer(Lot lot) {
        checkIfLotWon(lot);
        if (!isAuctionCreator(lot.getAuction())) {
            throw new AuctionIsNotOwnByUserException(lot.getAuction());
        }
    }

    public void validateBeforeConfirmationLotAccept(Lot lot) {
        checkIfLotWon(lot);
        if (!isLotWinner(lot)) {
            throw new UserIsNotLotWinnerException(lot);
        }
    }

    public void validateBeforeDeleting(Lot lot) {
        checkIfAuctionOwn(lot);
    }

    private boolean isLotWinner(Lot lot) {
        return lot.getWinner().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }

    private void checkIfLotWon(Lot lot) {
        if (!isLotWon(lot)) {
            throw new LotNotWonException(lot);
        }
    }

    private boolean isLotWon(Lot lot) {
        return lot.getWinner() != null;
    }

    public void validateBeforeCreating(Lot lot) {
        checkIfAuctionOwn(lot);
        checkIfNameExists(lot);
    }

    public void validateBeforeUpdating(Lot lot) {
        checkIfAuctionOwn(lot);
        checkIfNameExists(lot);
    }

    private void checkIfAuctionOwn(Lot lot) {
        Auction auction = auctionRepository
                .findById(lot.getAuction().getId())
                .orElseThrow(() -> new AuctionNotFoundException(lot.getAuction().getId()));
        if (isAuctionCreator(auction)) {
            throw new AuctionIsNotOwnByUserException(auction);
        }
    }

    private boolean isAuctionCreator(Auction auction) {
        return auction.getCreator().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }

    private void checkIfNameExists(Lot lot) {
        if (lotRepository.existsByName(lot.getName())) {
            throw new LotNameAlreadyExistsException(lot);
        }
    }
}
