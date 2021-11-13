package ru.netcracker.backend.service;

import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Log;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.util.LogLevel;

import java.util.List;

public interface LogService {
    List<LogResponse> getAuctionLogs(Long auctionId);

    void log(LogLevel level, Auction auction);
}
