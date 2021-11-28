package ru.netcracker.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.service.LogService;

import java.util.List;

@RestController
@RequestMapping("/api/auction/logs")
@CrossOrigin("*")
@Validated
public class LogController {
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/{id}")
    public List<LogResponse> getAuctionLogs(@PathVariable(name = "id") Long auctionId) {
        return logService.getAuctionLogs(auctionId);
    }
}
