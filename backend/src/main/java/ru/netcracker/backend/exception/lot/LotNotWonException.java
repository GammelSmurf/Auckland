package ru.netcracker.backend.exception.lot;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Lot;

public class LotNotWonException extends ValidationException {
    public LotNotWonException(Lot lot) {
        super(String.format("Lot with id: %d is not won", lot.getId()));
    }
}
