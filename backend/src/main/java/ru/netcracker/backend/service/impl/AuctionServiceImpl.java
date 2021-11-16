package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.NoLotsException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.AuctionStatus;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.util.AuctionUtil;
import ru.netcracker.backend.util.UserUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionResponse> getAllAuctions(Pageable pageable) {
        return auctionRepository
                .findAll(pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AuctionResponse createAuction(Auction auction) {
        return modelMapper.map(auctionRepository.save(updateCreator(auction)), AuctionResponse.class);
    }

    private Auction updateCreator(Auction auction) {
        auction.setCreator(userRepository
                .findByUsername(auction.getCreator().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, auction.getCreator().getUsername()))));
        return auction;
    }

    @Override
    public AuctionResponse updateAuction(Long id, Auction auction) {
        auction.setId(auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)))
                .getId());
        return modelMapper.map(auctionRepository.save(updateCreator(auction)), AuctionResponse.class);
    }

    @Override
    public void deleteAuction(Long id) {
        Auction auction = auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        auctionRepository.delete(auction);
    }

    @Override
    @Transactional(readOnly = true)
    public AuctionResponse getAuctionById(Long id) {
        return modelMapper.map(
                auctionRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id))),
                AuctionResponse.class);
    }

    @Override
    public void makeAuctionWaiting(Long id) throws NoLotsException, NotCorrectStatusException {
        Auction auction = auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        switch (auction.getStatus()) {
            case DRAFT:
                Optional<Lot> lotOptional = AuctionUtil.getAnotherLot(auction);
                if (lotOptional.isPresent()) {
                    auction.setStatus(AuctionStatus.WAITING);
                    auction.setCurrentLot(lotOptional.get());
                    auctionRepository.save(auction);
                } else {
                    throw new NoLotsException("Auction must have lots before waiting status");
                }
                break;
            case RUNNING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    @Override
    public UserResponse subscribe(String username, Long auctionId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username)));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));

        auction.getSubscribers().add(user);
        user.getSubscribedAuctions().add(auction);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }
}
