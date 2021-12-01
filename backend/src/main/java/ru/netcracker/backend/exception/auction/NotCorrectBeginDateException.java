package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;

public class NotCorrectBeginDateException extends ValidationException {
    public NotCorrectBeginDateException() {
        super("Auction's begin date must be in the future");
    }
}
