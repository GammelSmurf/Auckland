package ru.netcracker.backend.exception.bid;

import ru.netcracker.backend.exception.ValidationException;

public class NoCurrencyException extends ValidationException {
    public NoCurrencyException() {
        super("User don't have enough money");
    }
}
