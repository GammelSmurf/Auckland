package ru.netcracker.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.service.AuctionService;

import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepo;

    public AuctionServiceImpl(AuctionRepository auctionRepo) {
        this.auctionRepo = auctionRepo;
    }

    @Override
    public Page<Auction> getAllAuctions(Pageable pageable) {
        return auctionRepo.findAll(pageable);
    }

    @Override
    public Auction createAuction(Auction auction) {
        return auctionRepo.save(auction);
    }

    @Override
    public Auction updateAuction(long id, Auction auctionRequest) {
        Auction auction = auctionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));

        auction.setName(auctionRequest.getName());
        auction.setBeginDate(auctionRequest.getBeginDate());
        auction.setLotDuration(auctionRequest.getLotDuration());
        auction.setBoostTime(auctionRequest.getBoostTime());
        auction.setUsersLimit(auctionRequest.getUsersLimit());
        auction.setUser(auctionRequest.getUser());
        auction.setUserLikes(auctionRequest.getUserLikes());
        auction.setSubscribers(auctionRequest.getSubscribers());
        auction.setTags(auctionRequest.getTags());

        return auctionRepo.save(auction);
    }

    @Override
    public void deleteAuction(long id) {
        Auction auction = auctionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));

        auctionRepo.delete(auction);
    }

    @Override
    public Auction getAuctionById(long id) {
        Optional<Auction> result = auctionRepo.findById(id);
        if(result.isPresent()) {
            return result.get();
        }else {
            throw new ResourceNotFoundException("Post id: " + id);
        }
    }


}
