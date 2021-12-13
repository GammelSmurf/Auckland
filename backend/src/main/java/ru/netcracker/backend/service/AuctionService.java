package ru.netcracker.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.requests.SearchRequest;
import ru.netcracker.backend.model.responses.AuctionResponse;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.model.responses.UserResponse;

public interface AuctionService {
    Page<AuctionResponse> getAllAuctions(Pageable pageable);

    Page<AuctionResponse> searchAuctions(String username, SearchRequest searchRequest, int page, int size);

    AuctionResponse createAuction(Auction auction);

    AuctionResponse updateAuction(Long auctionId, Auction auction);

    void deleteAuction(Long auctionId);

    AuctionResponse getAuctionById(Long auctionId);

    void makeAuctionWaitingWithAnotherLot(Long auctionId);

    UserResponse subscribe(String username, Long auctionId);

    CategoryResponse addCategoryToAuction(Long auctionId, Long categoryId);

    AuctionResponse removeCategoryFromAuction(Long auctionId, Long categoryId);
}
