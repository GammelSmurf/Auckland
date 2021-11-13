package ru.netcracker.backend.service.impl;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.bet.*;
import ru.netcracker.backend.exception.user.NotSubscribedException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BetRepository;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.service.BetService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.util.AuctionUtil;
import ru.netcracker.backend.util.BetUtil;
import ru.netcracker.backend.util.LogLevel;
import ru.netcracker.backend.util.UserUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
public class BetServiceImpl implements BetService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final LotRepository lotRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;

    @Autowired
    public BetServiceImpl(BetRepository betRepository, UserRepository userRepository, AuctionRepository auctionRepository,
                          LotRepository lotRepository, LogService logService, ModelMapper modelMapper) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.lotRepository = lotRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
    }

    @Override
    public BetResponse makeBet(String username, Long auctionId, BigDecimal lotBank) throws ValidationException {
        return makeBetIfExist(
                userRepository.findByUsername(username).orElseThrow(
                        () -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username))),
                auctionRepository.findById(auctionId).orElseThrow(
                        () -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId))),
                lotBank
        );
    }

    private BetResponse makeBetIfExist(User user, Auction auction, BigDecimal lotBank)
            throws ValidationException {
        switch (auction.getStatus()) {
            case RUNNING:
                return handleBet(user, auction, lotBank);
            case DRAFT:
            case WAITING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private BetResponse handleBet(User user, Auction auction, BigDecimal lotBank)
            throws BankLessThanStepException,
            BankLessThanMinException, BankLessThanOldException, LotTimeExpiredException,
            NoCurrencyException, NotSubscribedException {
        BetUtil.validate(auction, lotBank, user);
        Bet bet = getUpdatedBet(user, auction, lotBank);
        logService.log(LogLevel.AUCTION_BET, bet.getAuction());
        return modelMapper.map(bet, BetResponse.class);
    }

    private Bet getUpdatedBet(User user, Auction auction, BigDecimal lotBank) {
        Bet bet = betRepository.findByAuction_Id(auction.getId()).orElse(new Bet(auction));
        bet.setCurrentBank(lotBank);
        bet.getLot().setEndTime(boostEndTime(bet));
        bet.setUser(user);
        auction.setBet(bet);
        auctionRepository.save(auction);
        return betRepository.save(bet);
    }

    private LocalDateTime boostEndTime(Bet oldBet) {
        return oldBet.getLot().getEndTime()
                .plus(oldBet.getAuction().getBoostTime().getSecond(), ChronoUnit.SECONDS);
    }

    @Override
    public String syncBeforeRun(Long auctionId) throws ValidationException {
        return syncBeforeRun(auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId))));
    }

    private String syncBeforeRun(Auction auction) throws ValidationException {
        switch (auction.getStatus()) {
            case WAITING:
                return getTimeBeforeRunAndSync(auction);
            case DRAFT:
            case RUNNING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private String getTimeBeforeRunAndSync(Auction auction) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (auction.getBeginDate().isAfter(currentDate)) {
            return formatTimeString(auction.getBeginDate(), currentDate);
        } else {
            makeAuctionRunning(auction);
            return BetUtil.RETURN_NULL_DATA_MSG;
        }
    }

    private void makeAuctionRunning(Auction auction) {
        auction.setStatus(AuctionStatus.RUNNING);
        generateAndSaveEndTime(auction);
        logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
    }

    @Override
    public String syncAfterRun(Long auctionId) throws ValidationException {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        return syncAfterRun(auction);
    }

    private String syncAfterRun(Auction auction) throws ValidationException {
        switch (auction.getStatus()) {
            case RUNNING:
                return getTimeAfterRunAndSync(auction);
            case DRAFT:
            case WAITING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private String getTimeAfterRunAndSync(Auction auction) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        if (currentTimestamp.isBefore(auction.getCurrentLot().getEndTime())) {
            return generateLotTime(auction, currentTimestamp);
        } else {
            if (isSold(auction)) {
                saveLotFinishWithWinner(auction);
            } else {
                saveLotFinish(auction);
            }

            if (getAnotherLot(auction).isEmpty()) {
                auction.setStatus(AuctionStatus.FINISHED);
                logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
                auctionRepository.save(auction);
            } else {
                setAndSaveAnotherLot(auction);
                logService.log(LogLevel.AUCTION_WINNER, auction);
            }
            return BetUtil.RETURN_NULL_DATA_MSG;
        }
    }

    private void setAndSaveAnotherLot(Auction auction) {
        auction.setCurrentLot(getAnotherLot(auction).orElseThrow());
        generateAndSaveEndTime(auction);
    }

    private void generateAndSaveEndTime(Auction auction) {
        auction.getCurrentLot().setEndTime(sumLocalDateAndTime(LocalDateTime.now(), auction.getLotDuration()));
        auctionRepository.save(auction);
    }

    private LocalDateTime sumLocalDateAndTime(LocalDateTime a, LocalTime b) {
        return a.plus(b.getSecond(), ChronoUnit.SECONDS);
    }

    public boolean isSold(Auction auction) {
        return auction.getBet() != null;
    }

    public String generateLotTime(Auction auction, LocalDateTime currentDate) {
        return formatTimeString(auction.getCurrentLot().getEndTime(), currentDate);
    }

    private String formatTimeString(LocalDateTime a, LocalDateTime b) {
        return formatTimeString(Duration.between(a, b));
    }

    private String formatTimeString(Duration duration) {
        return DurationFormatUtils.formatDuration(Math.abs(duration.toMillis()), BetUtil.RETURN_TIME_MSG_PATTERN);
    }

    private Optional<Lot> getAnotherLot(Auction auction) {
        return auction.getLots().stream().filter(lot -> !lot.isFinished()).findFirst();
    }

    private void saveLotFinishWithWinner(Auction auction) {
        auction.getBet().getLot().setWinner(auction.getBet().getUser());
        saveLotFinish(auction);
    }

    private void saveLotFinish(Auction auction) {
        auction.getCurrentLot().setFinished(true);
        lotRepository.save(auction.getCurrentLot());
    }
}
