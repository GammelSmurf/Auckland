package ru.netcracker.backend.exception.lot;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Lot;

public class LotNameAlreadyExistsException extends ValidationException {
    public LotNameAlreadyExistsException(Lot lot) {
        super(String.format("Lot with the name: %s already exists", lot.getName()));
    }
}
