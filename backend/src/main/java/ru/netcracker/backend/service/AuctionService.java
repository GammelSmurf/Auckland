package ru.netcracker.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.netcracker.backend.models.Auction;

public interface AuctionService {
    Page<Auction> getAllAuctions(Pageable pageable);
    ResponseEntity<Auction> getAuction(long auctionId);
    //ResponseEntity<Auction> createAuction();
}
