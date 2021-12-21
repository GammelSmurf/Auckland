package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;

public class LotTimeExpiredException extends ValidationException {
    public LotTimeExpiredException() {
        super("Lot time is expired");
    }
}
