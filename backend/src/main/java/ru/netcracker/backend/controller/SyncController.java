package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.responses.SyncResponse;
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

    @GetMapping("/{id}")
    public SyncResponse sync(@PathVariable Long id) throws ValidationException {
        return betService.sync(id);
    }
}
