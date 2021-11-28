package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.auction.NoLotsException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.requests.SubscribeRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.AuctionService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@CrossOrigin("*")
@Slf4j
@Validated
public class AuctionController {
    private final ModelMapper modelMapper;
    private final AuctionService auctionService;

    @Autowired
    public AuctionController(ModelMapper modelMapper, AuctionService auctionService) {
        this.modelMapper = modelMapper;
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<AuctionResponse> getAllAuctions(Pageable pageable) {
        return auctionService.getAllAuctions(pageable);
    }

    @GetMapping("/sub")
    public List<AuctionResponse> getAllSubscribedAuctions(@NotBlank @RequestParam("username") String username, Pageable pageable) {
        return auctionService.getAllSubscribedAuctions(username, pageable);
    }

    @GetMapping("/own")
    public List<AuctionResponse> getAllOwnAuctions(@NotBlank @RequestParam("username") String username, Pageable pageable) {
        return auctionService.getAllOwnAuctions(username, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable(name = "id") Long id) {
        AuctionResponse auctionResponse = auctionService.getAuctionById(id);
        log.info("sent auction: {}", auctionResponse);
        return new ResponseEntity<>(auctionResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@Valid @RequestBody AuctionRequest auctionRequest) {
        AuctionResponse auctionResponse = auctionService
                .createAuction(modelMapper.map(auctionRequest, Auction.class));

        log.info("created auction: {}", auctionRequest);
        return new ResponseEntity<>(auctionResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/available")
    public ResponseEntity<Void> makeAuctionAvailable(@PathVariable long id) throws NoLotsException, NotCorrectStatusException {
        auctionService.makeAuctionWaiting(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable Long id, @Valid @RequestBody AuctionRequest auctionRequest) {
        AuctionResponse auctionResponse = auctionService.updateAuction(
                id, modelMapper.map(auctionRequest, Auction.class));

        log.info("updated auction: {} with id: {}", auctionRequest, id);
        return new ResponseEntity<>(auctionResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable(name = "id") Long id) {
        auctionService.deleteAuction(id);

        log.info("deleted auction with id: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<UserResponse> subscribe(@Valid @RequestBody SubscribeRequest subscribeRequest) {
        UserResponse userResponse = auctionService.subscribe(
                subscribeRequest.getUsername(), subscribeRequest.getAuctionId());
        log.info("user: {} subscribed to auction with id: {}", userResponse.getUsername(), subscribeRequest.getAuctionId());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
