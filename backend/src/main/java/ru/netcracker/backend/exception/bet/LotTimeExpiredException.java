package ru.netcracker.backend.exception.bet;

import ru.netcracker.backend.exception.ValidationException;

public class LotTimeExpiredException extends ValidationException {
    public LotTimeExpiredException(String message) {
        super(message);
    }
}
