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
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.AuctionStatus;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BetRepository;
import ru.netcracker.backend.repository.LotRepository;
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
    public BetResponse makeBet(String username, Long auctionId, BigDecimal betBank) throws ValidationException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username)));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        switch (auction.getStatus()) {
            case RUNNING:
                BetUtil.validate(auction, betBank, user);
                Bet bet = makeAndSaveBet(user, auction, betBank);
                logService.log(LogLevel.AUCTION_BET, bet.getAuction());
                return modelMapper.map(bet, BetResponse.class);
            case DRAFT:
            case WAITING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private Bet makeAndSaveBet(User user, Auction auction, BigDecimal lotBank) {
        Bet bet = betRepository
                .findByAuction_Id(auction.getId())
                .orElse(new Bet(auction));
        bet.setCurrentBank(lotBank);
        bet.getLot().setEndTime(boostEndTime(bet));
        bet.setUser(user);
        betRepository.save(bet);

        auction.setBet(bet);
        return auctionRepository.save(auction).getBet();
    }

    private LocalDateTime boostEndTime(Bet oldBet) {
        return oldBet.getLot()
                .getEndTime()
                .plus(oldBet.getAuction()
                        .getBoostTime().getSecond(), ChronoUnit.SECONDS);
    }

    @Override
    public SyncResponse sync(Long auctionId) throws NotCorrectStatusException {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        LocalDateTime currentDate;
        switch (auction.getStatus()) {
            case WAITING:
                currentDate = LocalDateTime.now();
                if (auction.getBeginDate().isBefore(currentDate)) {
                    makeAuctionRun(auction);
                }
                return getSync(auction, currentDate, false, true);
            case RUNNING:
                currentDate = LocalDateTime.now();
                if (auction.getCurrentLot().getEndTime().isBefore(currentDate)) {
                    return handleLotFinished(auction, currentDate);
                } else {
                    return getSync(auction, currentDate, false, false);
                }
            case DRAFT:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    private void makeAuctionRun(Auction auction) {
        auction.setStatus(AuctionStatus.RUNNING);
        setNewEndTime(auction);
        logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
    }

    public SyncResponse handleLotFinished(Auction auction, LocalDateTime currentDate) {
        makeFinished(auction);
        if (AuctionUtil.getAnotherLot(auction).isEmpty()) {
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);
            logWinnerIfExists(auction);
            logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
            return getSync(auction, currentDate, false, false);
        } else {
            logWinnerIfExists(auction);
            return getSync(setAndSaveAnotherLot(auction), currentDate, true, false);
        }
    }

    private void logWinnerIfExists(Auction auction) {
        if (auction.getCurrentLot().getWinner() != null) {
            logService.log(LogLevel.AUCTION_WINNER, auction);
        }
    }

    private void makeFinished(Auction auction) {
        auction.getCurrentLot().setFinished(true);
        if (auction.getBet() != null) {
            auction.getCurrentLot().setWinner(auction.getBet().getUser());
            auction.getCurrentLot().setWinBank(auction.getBet().getCurrentBank());
            auction.setBet(null);
        }
        lotRepository.save(auction.getCurrentLot());
    }

    private Auction setAndSaveAnotherLot(Auction auction) {
        auction.setCurrentLot(AuctionUtil.getAnotherLot(auction)
                .orElseThrow());
        setNewEndTime(auction);
        return auctionRepository.save(auction);
    }

    private void setNewEndTime(Auction auction) {
        auction.getCurrentLot()
                .setEndTime(LocalDateTime.now()
                        .plus(auction.getLotDuration().getSecond(), ChronoUnit.SECONDS));
        lotRepository.save(auction.getCurrentLot());
    }

    private SyncResponse getSync(Auction auction, LocalDateTime currentDate, boolean changed, boolean until) {
        return new SyncResponse(
                DurationFormatUtils.formatDuration(Math.abs(getTime(auction, currentDate).toMillis()), BetUtil.RETURN_TIME_MSG_PATTERN),
                modelMapper.map(auction.getCurrentLot(), LotResponse.class),
                auction.getStatus(),
                changed,
                until);
    }

    private Duration getTime(Auction auction, LocalDateTime currentDate) {
        return getTime(auction.getCurrentLot().getEndTime(), currentDate);
    }

    private Duration getTime(LocalDateTime a, LocalDateTime b) {
        return (a == null)
                ? Duration.ZERO
                : Duration.between(a, b);

    }
}
