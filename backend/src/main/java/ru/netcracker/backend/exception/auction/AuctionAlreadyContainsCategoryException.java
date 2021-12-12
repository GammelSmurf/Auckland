package ru.netcracker.backend.exception.auction;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Category;

public class AuctionAlreadyContainsCategoryException extends ValidationException {
    public AuctionAlreadyContainsCategoryException(Category category) {
        super(String.format("Auction already contains category %s", category.getName()));
    }
}
