package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;
import ru.netcracker.backend.service.AuctionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin("*")
@Slf4j
public class AuctionController {

    private final ModelMapper modelMapper;

    private final AuctionService auctionService;

    public AuctionController(ModelMapper modelMapper, AuctionService auctionService) {
        this.modelMapper = modelMapper;
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<AuctionResponse> getAllAuctions(Pageable pageable) {
        return auctionService.getAllAuctions(pageable).stream().map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable(name = "id") Long id) {
        Auction auction = auctionService.getAuctionById(id);
        AuctionResponse auctionDto = modelMapper.map(auction, AuctionResponse.class);
        log.info("sent auction: " + auctionDto);
        return new ResponseEntity<>(auctionDto, HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@RequestBody AuctionRequest auctionDto) {
        Auction auctionRequest = modelMapper.map(auctionDto, Auction.class);

        Auction auction = auctionService.createAuction(auctionRequest);

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);
        log.info("created auction: " + auctionRequest);
        return new ResponseEntity<>(auctionResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponse> updateAuction(@PathVariable long id, @RequestBody AuctionRequest auctionDto) {
        Auction auctionRequest = modelMapper.map(auctionDto, Auction.class);
        Auction auction = auctionService.updateAuction(id, auctionRequest);

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);
        log.info("updated auction: " + auctionRequest + "with id: " + id);
        return new ResponseEntity<>(auctionResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable(name = "id") Long id) {
        auctionService.deleteAuction(id);
        log.info("deleted auction with id: " + id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
