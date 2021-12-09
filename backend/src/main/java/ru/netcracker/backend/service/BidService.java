package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.responses.BidResponse;
import ru.netcracker.backend.responses.SyncResponse;

import java.math.BigDecimal;

public interface BidService {
    BidResponse makeBid(String username, Long auctionId, BigDecimal lotBank) throws ValidationException;

    SyncResponse sync(Long auctionId);
}
