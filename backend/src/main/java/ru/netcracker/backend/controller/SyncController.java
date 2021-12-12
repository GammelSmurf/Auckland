package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.responses.SyncResponse;
import ru.netcracker.backend.service.BidService;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin("*")
@Slf4j
public class SyncController {
    private final BidService bidService;

    @Autowired
    public SyncController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/{id}")
    public SyncResponse sync(@PathVariable Long id) throws ValidationException {
        return bidService.handleAuctionProcess(id);
    }
}
