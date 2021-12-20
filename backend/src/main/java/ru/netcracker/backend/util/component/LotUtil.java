package ru.netcracker.backend.util.component;

import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.auction.AuctionIsNotOwnByUserException;
import ru.netcracker.backend.exception.lot.LotNotWonException;
import ru.netcracker.backend.exception.lot.UserIsNotLotWinnerException;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.util.SecurityUtil;

@Component
public class LotUtil {
    public void validateBeforeConfirmationLotTransfer(Lot lot) {
        checkIfLotWon(lot);
        if (!isAuctionCreator(lot)) {
            throw new AuctionIsNotOwnByUserException(lot.getAuction());
        }
    }

    public void validateBeforeConfirmationLotAccept(Lot lot) {
        checkIfLotWon(lot);
        if (!isLotWinner(lot)) {
            throw new UserIsNotLotWinnerException(lot);
        }
    }

    private boolean isAuctionCreator(Lot lot) {
        return lot.getAuction().getCreator().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
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
}
