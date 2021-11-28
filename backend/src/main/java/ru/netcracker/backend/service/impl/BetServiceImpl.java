package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.AuctionStatus;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BetRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.LotResponse;
import ru.netcracker.backend.responses.SyncResponse;
import ru.netcracker.backend.service.BetService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.util.AuctionUtil;
import ru.netcracker.backend.util.BetUtil;
import ru.netcracker.backend.util.LogLevel;
import ru.netcracker.backend.util.UserUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional(readOnly = true)
public class BetServiceImpl implements BetService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;

    @Autowired
    public BetServiceImpl(BetRepository betRepository, UserRepository userRepository, AuctionRepository auctionRepository,
                          LogService logService, ModelMapper modelMapper) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BetResponse makeBet(String username, Long auctionId, BigDecimal betBank) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username)));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        switch (auction.getStatus()) {
            case RUNNING:
                BetUtil.validate(auction, betBank, user);
                Bet bet = (auction.getBet() != null)
                        ? auction.getBet()
                        : new Bet(auction);
                bet.setCurrentBank(betBank);
                bet.getLot().setEndTime(boostEndTime(bet));
                bet.setUser(user);
                bet.setAuction(auction);
                auction.setBet(bet);
                logService.log(LogLevel.AUCTION_BET, bet.getAuction());
                return modelMapper.map(betRepository.save(bet), BetResponse.class);
            case DRAFT:
            case WAITING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private LocalDateTime boostEndTime(Bet oldBet) {
        return oldBet.getLot()
                .getEndTime()
                .plus(oldBet.getAuction()
                        .getBoostTime().getNano(), ChronoUnit.NANOS);
    }

    @Override
    @Transactional
    public SyncResponse sync(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        LocalDateTime currentDate = LocalDateTime.now();
        switch (auction.getStatus()) {
            case WAITING:
                if (currentDate.isAfter(auction.getBeginDate()) || currentDate.isEqual(auction.getBeginDate())) {
                    auction.setStatus(AuctionStatus.RUNNING);
                    setNewEndTime(auction, currentDate);
                    logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
                }
                return generateSyncResponse(auction, currentDate, false);
            case RUNNING:
                if (currentDate.isAfter(auction.getCurrentLot().getEndTime()) || currentDate.isEqual(auction.getCurrentLot().getEndTime())) {
                    return handleLotFinished(auction, currentDate);
                } else {
                    return generateSyncResponse(auction, currentDate, false);
                }
            case FINISHED:
                return generateSyncResponse(auction, currentDate, false);
            case DRAFT:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    public SyncResponse handleLotFinished(Auction auction, LocalDateTime currentDate) {
        auction.getCurrentLot().setFinished(true);
        if (auction.getBet() != null) {
            auction.getCurrentLot().setWinner(auction.getBet().getUser());
            auction.getCurrentLot().setWinBank(auction.getBet().getCurrentBank());
            betRepository.delete(auction.getBet());
        }

        if (AuctionUtil.getAnotherLot(auction).isEmpty()) {
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);

            logWinnerIfExists(auction);
            logService.log(LogLevel.AUCTION_STATUS_CHANGE, auction);
            return generateSyncResponse(auction, currentDate, false);
        } else {
            logWinnerIfExists(auction);
            return generateSyncResponse(setAndSaveAnotherLot(auction, currentDate), currentDate, true);
        }
    }

    private void logWinnerIfExists(Auction auction) {
        if (auction.getCurrentLot().getWinner() != null) {
            logService.log(LogLevel.AUCTION_WINNER, auction);
        }
    }

    private Auction setAndSaveAnotherLot(Auction auction, LocalDateTime currentDate) {
        auction.setCurrentLot(AuctionUtil.getAnotherLot(auction)
                .orElseThrow());
        setNewEndTime(auction, currentDate);
        return auctionRepository.save(auction);
    }

    private void setNewEndTime(Auction auction, LocalDateTime currentDate) {
        auction.getCurrentLot()
                .setEndTime(currentDate
                        .plus(auction.getLotDuration().toNanoOfDay(), ChronoUnit.NANOS));
    }

    private SyncResponse generateSyncResponse(Auction auction, LocalDateTime currentDate, boolean changed) {
        return new SyncResponse(
                getDurationInSec(auction, currentDate),
                modelMapper.map(auction.getCurrentLot(), LotResponse.class),
                auction.getStatus(),
                (auction.getBet() != null) ? auction.getBet().getCurrentBank() : null,
                changed);
    }

    private Long getDurationInSec(Auction auction, LocalDateTime currentDate) {
        switch (auction.getStatus()) {
            case WAITING:
                return getDurationInSec(auction.getBeginDate(), currentDate);
            case RUNNING:
                return getDurationInSec(auction.getCurrentLot().getEndTime(), currentDate);
            case FINISHED:
                return Duration.ZERO.toSeconds();
            case DRAFT:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private Long getDurationInSec(LocalDateTime from, LocalDateTime to) {
        return Math.abs(Duration.between(from, to).toSeconds());
    }
}
