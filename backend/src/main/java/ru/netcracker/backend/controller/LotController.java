package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.domain.Lot;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.requests.LotRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;
import ru.netcracker.backend.models.responses.LotResponse;
import ru.netcracker.backend.service.LotService;

@RestController
@RequestMapping("/api/lot")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class LotController {
    private final ModelMapper modelMapper;
    private final LotService lotService;

    @PostMapping
    public ResponseEntity<LotResponse> createLot(@RequestBody LotRequest lotDto) {
        Lot lotRequest = modelMapper.map(lotDto, Lot.class);
        Lot lot = lotService.createLot(lotRequest);
        LotResponse lotResponse = modelMapper.map(lot, LotResponse.class);
        log.info("created lot: {}", lotRequest);
        return new ResponseEntity<>(lotResponse, HttpStatus.CREATED);
    }
}
