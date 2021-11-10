package ru.netcracker.backend.service;

import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.AuctionLog;

import java.util.List;

public interface AuctionLogService {
    List<AuctionLog> getAuctionLogs(long auctionId);
    void logBet(Long auctionId, AuctionProcess auctionProcess);
    void logChange(Long auctionId, Auction auction);
}
