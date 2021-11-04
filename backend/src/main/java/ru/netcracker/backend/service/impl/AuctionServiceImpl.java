package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.auction.AuctionStatus;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.service.AuctionService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    @Override
    public Page<Auction> getAllAuctions(Pageable pageable) {
        return auctionRepository.findAll(pageable);
    }

    @Override
    public Auction createAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    @Override
    public Auction updateAuction(long id, Auction auctionRequest) {
        Auction auction =
                auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Auction with id: " + id + " was not found"));

        auction.setName(auctionRequest.getName());
        auction.setBeginDate(auctionRequest.getBeginDate());
        auction.setLotDuration(auctionRequest.getLotDuration());
        auction.setBoostTime(auctionRequest.getBoostTime());
        auction.setUsersLimit(auctionRequest.getUsersLimit());
        auction.setUser(auctionRequest.getUser());
        auction.setUserLikes(auctionRequest.getUserLikes());
        auction.setSubscribers(auctionRequest.getSubscribers());
        auction.setTags(auctionRequest.getTags());
        auction.setStatus(auctionRequest.getStatus());

        return auctionRepository.save(auction);
    }

    @Override
    public void deleteAuction(long id) {
        Auction auction =
                auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));

        auctionRepository.delete(auction);
    }

    @Override
    public Auction getAuctionById(long id) {
        Optional<Auction> result = auctionRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new ResourceNotFoundException("Post id: " + id);
        }
    }

    @Override
    public void makeAuctionAvailable(long id) {
        Auction auction =
                auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));
        auction.setStatus(AuctionStatus.WAITING);

        auctionRepository.save(auction);
    }
}
