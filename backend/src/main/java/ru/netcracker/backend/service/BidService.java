package ru.netcracker.backend.service;

import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.responses.BidResponse;
import ru.netcracker.backend.model.responses.SyncResponse;

import java.math.BigDecimal;

public interface BidService {
    BidResponse makeBid(String username, Long auctionId, BigDecimal amount) throws ValidationException;

    SyncResponse handleAuctionProcess(Long auctionId);
}
