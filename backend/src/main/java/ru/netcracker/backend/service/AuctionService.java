package ru.netcracker.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.requests.SearchRequest;
import ru.netcracker.backend.model.responses.AuctionResponse;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.model.responses.UserResponse;

import java.util.List;

public interface AuctionService {
    Page<AuctionResponse> getAllAuctions(Pageable pageable);

    List<AuctionResponse> getAllIfCreator();

    Page<AuctionResponse> searchAuctions(SearchRequest searchRequest, int page, int size);

    AuctionResponse createAuction(Auction auction);

    AuctionResponse updateAuction(Long auctionId, Auction auction);

    void deleteAuction(Long auctionId);

    AuctionResponse getAuctionById(Long auctionId);

    void makeAuctionWaitingWithAnotherLot(Long auctionId);

    UserResponse subscribe(Long auctionId);

    CategoryResponse addCategoryToAuction(Long auctionId, Long categoryId);

    AuctionResponse removeCategoryFromAuction(Long auctionId, Long categoryId);

    AuctionResponse addLike(Long auctionId);

    AuctionResponse removeLike(Long auctionId);
}
