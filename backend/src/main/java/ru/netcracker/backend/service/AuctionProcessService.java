package ru.netcracker.backend.service;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import ru.netcracker.backend.model.AuctionProcess;

public interface AuctionProcessService {
    AuctionProcess createAuctionProcess(AuctionProcess auctionProcess);

    AuctionProcess getAuctionProcess(Long auctionId);

    AuctionProcess updateAuctionProcess(long id, AuctionProcess auctionProcess);

    String getRemainingTime(Long id);
}
