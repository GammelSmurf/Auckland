package ru.netcracker.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.models.Auction;
import ru.netcracker.backend.repository.AuctionRepo;
import ru.netcracker.backend.service.AuctionService;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepo auctionRepo;

    public AuctionServiceImpl(AuctionRepo auctionRepo) {
        this.auctionRepo = auctionRepo;
    }

    @Override
    public Page<Auction> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepo.findAll(pageable);
        return auctions;
    }

    @Override
    public Auction getAuction(long auctionId) {
        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction with id " + auctionId + " was not found."));
        return auction;
    }
}
