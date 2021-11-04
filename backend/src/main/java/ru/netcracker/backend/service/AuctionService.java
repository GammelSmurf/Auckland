package ru.netcracker.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.netcracker.backend.model.auction.Auction;

public interface AuctionService {
    Page<Auction> getAllAuctions(Pageable pageable);

    Auction createAuction(Auction auction);

    Auction updateAuction(long id, Auction auction);

    void deleteAuction(long id);

    Auction getAuctionById(long id);

    void makeAuctionAvailable(long id);
}
