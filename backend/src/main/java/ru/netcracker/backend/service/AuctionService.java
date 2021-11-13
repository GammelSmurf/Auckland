package ru.netcracker.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.netcracker.backend.exception.auction.NoLotsException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.UserResponse;

import java.util.List;

public interface AuctionService {
    List<AuctionResponse> getAllAuctions(Pageable pageable);

    AuctionResponse createAuction(Auction auction);

    AuctionResponse updateAuction(Long id, Auction auction);

    void deleteAuction(Long id);

    AuctionResponse getAuctionById(Long id);

    void makeAuctionWaiting(Long id) throws NoLotsException;

    UserResponse subscribe(String username, Long auctionId);
}
