package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;

public class AmountIsNegativeOrBlankException extends ValidationException {
    public AmountIsNegativeOrBlankException() {
        super("Bid amount can't be negative or blank");
    }
}
