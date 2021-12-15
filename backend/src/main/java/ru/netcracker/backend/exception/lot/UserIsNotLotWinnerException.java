package ru.netcracker.backend.exception.lot;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.util.SecurityUtil;

public class UserIsNotLotWinnerException extends ValidationException {
    public UserIsNotLotWinnerException(Lot lot) {
        super(String.format("Lot with id: %d is not won by: %s", lot.getId(), SecurityUtil.getUsernameFromSecurityCtx()));
    }
}
