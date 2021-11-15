package ru.netcracker.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.service.LogService;

import java.util.List;

@RestController
@RequestMapping("/api/auction/logs")
@CrossOrigin("*")
public class LogController {
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public List<LogResponse> getAuctionLogs(long auctionId) {
        return logService.getAuctionLogs(auctionId);
    }
}
