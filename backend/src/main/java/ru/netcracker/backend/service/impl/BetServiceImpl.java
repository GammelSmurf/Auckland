package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.*;
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
import java.util.Comparator;

@Service
@Transactional(readOnly = true)
public class BetServiceImpl implements BetService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final TransactionRepository transactionRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;

    @Autowired
    public BetServiceImpl(BetRepository betRepository, UserRepository userRepository, AuctionRepository auctionRepository,
                          TransactionRepository transactionRepository, LogService logService, ModelMapper modelMapper) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.transactionRepository = transactionRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void handleCancelTransactions() {
        for (Transaction tx : transactionRepository.findAllByTransactionStatus(TransactionStatus.CANCEL)) {
            tx.getUser().addCurrency(tx.getCurrentBank());
            transactionRepository.delete(tx);
        }
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
                user.subtractCurrency(betBank);
                bet.setUser(user);
                bet.getLot().setEndTime(boostEndTime(bet));
                bet.setAuction(auction);
                bet.getTransactions().add(transactionRepository.save(new Transaction(bet)));
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
                        .getBoostTime().toNanoOfDay(), ChronoUnit.NANOS);
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

            auction.getBet().getTransactions().stream()
                    .max(Comparator.comparing(Transaction::getDatetime))
                    .ifPresent(tx -> tx.setTransactionStatus(TransactionStatus.DONE));

            betRepository.delete(auction.getBet());
        }
        auction.getBet().getTransactions().stream()
                .filter(tx -> !tx.getTransactionStatus().equals(TransactionStatus.DONE))
                .forEach(tx -> tx.setTransactionStatus(TransactionStatus.CANCEL));

        if (AuctionUtil.getAnotherLot(auction).isEmpty()) {
            auction.setStatus(AuctionStatus.FINISHED);
            auction.setEndDate(currentDate);
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
        } else {
            logService.log(LogLevel.AUCTION_NO_WINNER, auction);
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
