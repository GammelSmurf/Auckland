package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.AuctionNotFoundException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BidRepository;
import ru.netcracker.backend.repository.TransactionRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.responses.BidResponse;
import ru.netcracker.backend.responses.LotResponse;
import ru.netcracker.backend.responses.SyncResponse;
import ru.netcracker.backend.service.BidService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.util.BidUtil;
import ru.netcracker.backend.util.LogLevel;
import ru.netcracker.backend.util.NotificationLevel;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Service
@Transactional(readOnly = true)
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final TransactionRepository transactionRepository;
    private final LogService logService;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @Autowired
    public BidServiceImpl(BidRepository bidRepository, UserRepository userRepository, AuctionRepository auctionRepository,
                          TransactionRepository transactionRepository, LogService logService, NotificationService notificationService, ModelMapper modelMapper) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.transactionRepository = transactionRepository;
        this.logService = logService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BidResponse makeBid(String username, Long auctionId, BigDecimal betBank) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        BidUtil.validate(auction, betBank, user);
        Bid bid = (auction.getBid() != null)
                ? auction.getBid()
                : new Bid(auction);
        bid.setAmount(betBank);
        user.subtractCurrency(betBank);
        bid.setUser(user);
        bid.getLot().setEndTime(boostEndTime(bid));
        bid.setAuction(auction);
        bid.getTransactions().add(transactionRepository.save(new Transaction(bid)));
        auction.setBid(bid);
        logService.log(LogLevel.AUCTION_BET, bid.getAuction());
        return modelMapper.map(bidRepository.save(bid), BidResponse.class);

    }

    private LocalDateTime boostEndTime(Bid oldBet) {
        return oldBet.getLot()
                .getEndTime()
                .plus(oldBet.getAuction()
                        .getBoostTime().toNanoOfDay(), ChronoUnit.NANOS);
    }

    @Override
    @Transactional
    public SyncResponse sync(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        LocalDateTime currentDate = LocalDateTime.now();
        switch (auction.getStatus()) {
            case WAITING:
                if (currentDate.isAfter(auction.getBeginDate()) || currentDate.isEqual(auction.getBeginDate())) {
                    auction.setStatus(AuctionStatus.RUNNING);
                    setNewEndTime(auction, currentDate);
                    logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
                    notificationService.log(NotificationLevel.SUBSCRIBED_AUCTION_STATUS_CHANGED, null, auction);
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
        if (auction.getBid() != null) {
            auction.getCurrentLot().setWinner(auction.getBid().getUser());
            auction.getCurrentLot().setWinPrice(auction.getBid().getAmount());

            auction.getBid().getTransactions().stream()
                    .max(Comparator.comparing(Transaction::getDateTime))
                    .ifPresent(tx -> tx.setTransactionStatus(TransactionStatus.WAIT));
            auction.getBid().getTransactions().stream()
                    .filter(tx -> !tx.getTransactionStatus().equals(TransactionStatus.WAIT))
                    .forEach(tx -> {
                        tx.getBuyer().addCurrency(tx.getPrice());
                        transactionRepository.delete(tx);
                    });

            bidRepository.delete(auction.getBid());
        }

        if (auction.getAnotherLot().isEmpty()) {
            auction.setStatus(AuctionStatus.FINISHED);
            auction.setEndDate(currentDate);
            auctionRepository.save(auction);

            logWinnerIfExists(auction);
            logService.log(LogLevel.AUCTION_STATUS_CHANGE, auction);
            notificationService.log(NotificationLevel.SUBSCRIBED_AUCTION_STATUS_CHANGED, null, auction);
            return generateSyncResponse(auction, currentDate, false);
        } else {
            logWinnerIfExists(auction);
            return generateSyncResponse(setAndSaveAnotherLot(auction, currentDate), currentDate, true);
        }
    }

    private void logWinnerIfExists(Auction auction) {
        if (auction.getCurrentLot().getWinner() != null) {
            logService.log(LogLevel.AUCTION_WINNER, auction);
        } else {
            logService.log(LogLevel.AUCTION_NO_WINNER, auction);
        }
    }

    private Auction setAndSaveAnotherLot(Auction auction, LocalDateTime currentDate) {
        auction.setAnotherLot();
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
                (auction.getBid() != null) ? auction.getBid().getAmount() : null,
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
