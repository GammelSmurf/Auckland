package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.responses.LogResponse;
import ru.netcracker.backend.util.enumiration.LogLevel;

import java.util.List;

public interface LogService {
    List<LogResponse> getAuctionLogs(Long auctionId);

    void log(LogLevel level, Auction auction);
}
