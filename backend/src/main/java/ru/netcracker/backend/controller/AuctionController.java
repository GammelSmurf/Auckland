package ru.netcracker.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.models.Auction;
import ru.netcracker.backend.service.AuctionService;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin("*")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public Page<Auction> getAllAuctions(Pageable pageable) {
        return auctionService.getAllAuctions(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(auctionService.getAuction(id), HttpStatus.FOUND);
    }
}
