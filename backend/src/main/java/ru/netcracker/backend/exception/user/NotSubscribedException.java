package ru.netcracker.backend.exception.user;

import ru.netcracker.backend.exception.ValidationException;

public class NotSubscribedException extends ValidationException {
    public NotSubscribedException() {
        super("The user is not subscribed to the auction");
    }
}
