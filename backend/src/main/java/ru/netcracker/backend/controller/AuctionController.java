package ru.netcracker.backend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.dto.AuctionDto;
import ru.netcracker.backend.service.AuctionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin("*")
public class AuctionController {

    private final ModelMapper modelMapper;

    private final AuctionService auctionService;

    public AuctionController(ModelMapper modelMapper, AuctionService auctionService) {
        this.modelMapper = modelMapper;
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<AuctionDto> getAllAuctions(Pageable pageable) {
        return auctionService.getAllAuctions(pageable).stream().map(auction -> modelMapper.map(auction, AuctionDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionDto> getAuction(@PathVariable(name = "id") Long id) {
        Auction auction = auctionService.getAuctionById(id);
        AuctionDto auctionDto = modelMapper.map(auction, AuctionDto.class);
        return new ResponseEntity<>(auctionDto, HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<AuctionDto> createAuction(@RequestBody AuctionDto auctionDto) {
        Auction auctionRequest = modelMapper.map(auctionDto, Auction.class);

        Auction auction = auctionService.createAuction(auctionRequest);

        AuctionDto auctionResponse = modelMapper.map(auction, AuctionDto.class);
        return new ResponseEntity<>(auctionResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionDto> updateAuction(@PathVariable long id, @RequestBody AuctionDto auctionDto) {
        Auction auctionRequest = modelMapper.map(auctionDto, Auction.class);
        Auction auction = auctionService.updateAuction(id, auctionRequest);

        AuctionDto auctionResponse = modelMapper.map(auction, AuctionDto.class);
        return new ResponseEntity<>(auctionResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable(name = "id") Long id) {
        auctionService.deleteAuction(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
