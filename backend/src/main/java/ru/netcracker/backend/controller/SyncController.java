package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.service.BetService;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin("*")
@Slf4j
public class SyncController {
    private final BetService betService;

    @Autowired
    public SyncController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping("/sync/before/{id}")
    public String syncBeforeRun(@PathVariable Long id) throws ValidationException {
        return betService.syncBeforeRun(id);
    }

    @GetMapping("/sync/after/{id}")
    public String syncAfterRun(@PathVariable Long id) throws ValidationException {
        return betService.syncAfterRun(id);
    }
}
