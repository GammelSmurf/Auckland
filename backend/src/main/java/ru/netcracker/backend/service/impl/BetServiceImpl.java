package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.bet.BankLessThanMinException;
import ru.netcracker.backend.exception.bet.BankLessThanOldException;
import ru.netcracker.backend.exception.bet.BankLessThanStepException;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.auction.AuctionStatus;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.BetRepository;
import ru.netcracker.backend.service.AuctionLogService;
import ru.netcracker.backend.service.BetService;
import ru.netcracker.backend.util.BetUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class BetServiceImpl implements BetService {
    private final BetRepository betRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionLogService auctionLogService;

    @Override
    public Bet createBet(Bet bet) {
        return betRepository.save(bet);
    }

    @Override
    public Bet getBet(Long auctionId) {
        return betRepository
                .findBetByAuctionId(auctionId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Auction with id: " + auctionId + " was not found"));
    }

    @Override
    public Bet makeBet(long auctionId, Bet newBet)
            throws BankLessThanStepException, BankLessThanMinException, BankLessThanOldException {
        Bet oldBet =
                betRepository
                        .findBetByAuctionId(auctionId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Post auction process with auction id: "
                                                        + auctionId
                                                        + " was not found"));

        BetUtil.validate(oldBet, newBet);
        oldBet.setCurrentBank(newBet.getCurrentBank());
        oldBet.setRemainingTime(newBet.getRemainingTime());

        auctionLogService.logBet(auctionId, oldBet);
        return betRepository.save(oldBet);
    }

    @Override
    public String getRemainingTime(long auctionId) {
        Auction auction = auctionRepository.getById(auctionId);
        auction.getBeginDate();
        Timestamp beginDate = auction.getBeginDate();
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());

        if (beginDate.after(currentDate)) {
            return new SimpleDateFormat("HH:mm:ss")
                    .format(new Timestamp(beginDate.getTime() - currentDate.getTime()));
        }

        auction.setStatus(AuctionStatus.RUNNING);
        auctionLogService.logChange(auctionId, auctionRepository.save(auction));
        return "00:00:00";
    }
}
