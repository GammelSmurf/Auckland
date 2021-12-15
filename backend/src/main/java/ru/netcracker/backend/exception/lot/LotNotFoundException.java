package ru.netcracker.backend.exception.lot;

import ru.netcracker.backend.exception.ValidationException;

public class LotNotFoundException extends ValidationException {
    public LotNotFoundException(Long lotId) {
        super(String.format("Lot with id: %d was not found", lotId));
    }
}
