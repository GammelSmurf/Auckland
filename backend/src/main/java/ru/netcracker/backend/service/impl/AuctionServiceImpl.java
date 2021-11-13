package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.NoLotsException;
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
        return auctionRepository.findAll(pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AuctionResponse createAuction(Auction auction) {
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    @Override
    public AuctionResponse updateAuction(Long id, Auction auction) {
        Auction auctionToUpdate = auctionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        auction.setId(auctionToUpdate.getId());
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    @Override
    public void deleteAuction(Long id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        auctionRepository.delete(auction);
    }

    @Override
    @Transactional(readOnly = true)
    public AuctionResponse getAuctionById(Long id) {
        return modelMapper.map(
                auctionRepository.findById(id).orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id))),
                AuctionResponse.class);
    }

    @Override
    public void makeAuctionWaiting(Long id) throws NoLotsException {
        Auction auction = auctionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        Optional<Lot> lotOptional = getAnotherLot(auction);
        if (lotOptional.isPresent()) {
            auction.setStatus(AuctionStatus.WAITING);
            auction.setCurrentLot(lotOptional.get());
            auctionRepository.save(auction);
        } else {
            throw new NoLotsException("Auction must have lots before waiting status");
        }
    }

    private Optional<Lot> getAnotherLot(Auction auction) {
        return auction.getLots().stream().filter(lot -> !lot.isFinished()).findFirst();
    }

    @Override
    public UserResponse subscribe(String username, Long auctionId) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username)));
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        user.getSubscribes().add(auction);
        User userTo = userRepository.save(user);
        return modelMapper.map(userTo, UserResponse.class);
    }
}
