package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netcracker.backend.responses.AuctionLogResponse;
import ru.netcracker.backend.service.AuctionLogService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auctions/logs")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuctionLogsController {
    private final AuctionLogService auctionLogService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<AuctionLogResponse> getAuctionLogs(long auctionId) {
        return auctionLogService.getAuctionLogs(auctionId).stream()
                .map(auctionLog -> modelMapper.map(auctionLog, AuctionLogResponse.class))
                .collect(Collectors.toList());
    }
}
