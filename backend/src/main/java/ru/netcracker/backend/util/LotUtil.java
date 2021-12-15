package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.auction.AuctionIsNotOwnByUserException;
import ru.netcracker.backend.exception.lot.LotNotWonException;
import ru.netcracker.backend.exception.lot.UserIsNotLotWinnerException;
import ru.netcracker.backend.model.entity.Lot;

public final class LotUtil {
    public static void validateBeforeConfirmationLotTransfer(Lot lot) {
        checkIfLotWon(lot);
        if (!isAuctionCreator(lot)) {
            throw new AuctionIsNotOwnByUserException(lot.getAuction());
        }
    }

    public static void validateBeforeConfirmationLotAccept(Lot lot) {
        checkIfLotWon(lot);
        if (!isLotWinner(lot)) {
            throw new UserIsNotLotWinnerException(lot);
        }
    }

    private static boolean isAuctionCreator(Lot lot) {
        return lot.getAuction().getCreator().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }

    private static boolean isLotWinner(Lot lot) {
        return lot.getWinner().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }

    private static void checkIfLotWon(Lot lot) {
        if (!isLotWon(lot)) {
            throw new LotNotWonException(lot);
        }
    }

    private static boolean isLotWon(Lot lot) {
        return lot.getWinner() != null;
    }
}
