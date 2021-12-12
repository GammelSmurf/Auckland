package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.AuctionNotFoundException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.entity.*;
import ru.netcracker.backend.model.responses.BidResponse;
import ru.netcracker.backend.model.responses.LotResponse;
import ru.netcracker.backend.model.responses.SyncResponse;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BidRepository;
import ru.netcracker.backend.repository.TransactionRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.service.BidService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.util.BidUtil;
import ru.netcracker.backend.util.enumiration.LogLevel;
import ru.netcracker.backend.util.enumiration.NotificationLevel;

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
    public BidResponse makeBid(String username, Long auctionId, BigDecimal amount) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        BidUtil.validate(auction, amount, user);
        Bid bid = makeBit(auction, user, amount);
        createTransaction(bid);
        logService.log(LogLevel.AUCTION_BET, bid.getAuction());
        return modelMapper.map(bidRepository.save(bid), BidResponse.class);

    }

    private Bid makeBit(Auction auction, User user, BigDecimal amount) {
        Bid bid = (auction.getCurrentBid() != null)
                ? auction.getCurrentBid()
                : new Bid(auction);
        bid.updateWith(amount, user, auction);
        return bid;
    }

    private void createTransaction(Bid bid) {
        bid.getTransactions().add(transactionRepository.save(new Transaction(bid)));
    }

    @Override
    @Transactional
    public SyncResponse handleAuctionProcess(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        LocalDateTime currentDateTime = LocalDateTime.now();
        switch (auction.getStatus()) {
            case WAITING:
                return handleAuctionProcessInWaitingStatus(auction, currentDateTime);
            case RUNNING:
                return handleAuctionProcessInRunningStatus(auction, currentDateTime);
            case FINISHED:
                return handleAuctionProcessInFinishedStatus(auction, currentDateTime);
            case DRAFT:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private SyncResponse handleAuctionProcessInWaitingStatus(Auction auction, LocalDateTime currentDateTime) {
        if (isAfterOrEqual(currentDateTime, auction.getBeginDateTime())) {
            auction.setRunningStatus();
            setNewEndTime(auction, currentDateTime);

            sendLogAndNotificationAboutChangingAuctionStatus(auction);
        }
        return generateSyncResponse(auction, currentDateTime, false);
    }

    private SyncResponse handleAuctionProcessInRunningStatus(Auction auction, LocalDateTime currentDateTime) {
        if (isAfterOrEqual(currentDateTime, auction.getCurrentLot().getEndDateTime())) {
            return handleLotFinished(auction, currentDateTime);
        } else {
            return generateSyncResponse(auction, currentDateTime, false);
        }
    }

    private SyncResponse handleAuctionProcessInFinishedStatus(Auction auction, LocalDateTime currentDateTime) {
        return generateSyncResponse(auction, currentDateTime, false);
    }

    private boolean isAfterOrEqual(LocalDateTime one, LocalDateTime other) {
        return one.isAfter(other) || one.isEqual(other);
    }

    public SyncResponse handleLotFinished(Auction auction, LocalDateTime currentDateTime) {
        auction.getCurrentLot().setFinished(true);
        handleWinnerIfExists(auction);
        return handleIfAuctionFinished(auction, currentDateTime);
    }

    private void handleWinnerIfExists(Auction auction) {
        if (hasWinner(auction)) {
            makeLotWon(auction);
            freezeLastTransaction(auction);
            refundMoneyAndDeleteAllTransactionsForBidExceptFrozen(auction);
            bidRepository.delete(auction.getCurrentBid());
        }
    }

    private SyncResponse handleIfAuctionFinished(Auction auction, LocalDateTime currentDateTime) {
        if (auction.getAnotherLot().isEmpty()) {
            auction.setFinishedStatus();
            auction.setEndDateTime(currentDateTime);
            auctionRepository.save(auction);

            logWinnerIfExists(auction);
            sendLogAndNotificationAboutChangingAuctionStatus(auction);
            return generateSyncResponse(auction, currentDateTime, false);
        } else {
            logWinnerIfExists(auction);
            return generateSyncResponse(setAndSaveAnotherLot(auction, currentDateTime), currentDateTime, true);
        }
    }

    private void sendLogAndNotificationAboutChangingAuctionStatus(Auction auction) {
        logService.log(LogLevel.AUCTION_STATUS_CHANGE, auction);
        notificationService.log(NotificationLevel.SUBSCRIBED_AUCTION_STATUS_CHANGED, null, auction);
    }

    private boolean hasWinner(Auction auction) {
        return auction.getCurrentBid() != null;
    }

    private void makeLotWon(Auction auction) {
        auction.getCurrentLot().setWinner(auction.getCurrentBid().getUser());
        auction.getCurrentLot().setWinPrice(auction.getCurrentBid().getAmount());
    }

    private void freezeLastTransaction(Auction auction) {
        auction.getCurrentBid().getTransactions().stream()
                .max(Comparator.comparing(Transaction::getDateTime))
                .ifPresent(tx -> tx.setTransactionStatus(TransactionStatus.FROZEN));
    }

    private void refundMoneyAndDeleteAllTransactionsForBidExceptFrozen(Auction auction) {
        auction.getCurrentBid().getTransactions().stream()
                .filter(tx -> !tx.getTransactionStatus().equals(TransactionStatus.FROZEN))
                .forEach(tx -> {
                    tx.getBuyer().addMoney(tx.getAmount());
                    transactionRepository.delete(tx);
                });
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
                .setEndDateTime(currentDate
                        .plus(auction.getLotDurationTime().toNanoOfDay(), ChronoUnit.NANOS));
    }

    private SyncResponse generateSyncResponse(Auction auction, LocalDateTime currentDate, boolean changed) {
        return SyncResponse.builder()
                .secondsUntil(getDurationInSec(auction, currentDate))
                .currentLot(modelMapper.map(auction.getCurrentLot(), LotResponse.class))
                .auctionStatus(auction.getStatus())
                .amount((auction.getCurrentBid() != null) ? auction.getCurrentBid().getAmount() : null)
                .changed(changed)
                .build();
    }

    private Long getDurationInSec(Auction auction, LocalDateTime currentDate) {
        switch (auction.getStatus()) {
            case WAITING:
                return getDurationInSec(auction.getBeginDateTime(), currentDate);
            case RUNNING:
                return getDurationInSec(auction.getCurrentLot().getEndDateTime(), currentDate);
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
