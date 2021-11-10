package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.auction.AuctionStatus;
import ru.netcracker.backend.repository.AuctionProcessRepository;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.service.AuctionLogService;
import ru.netcracker.backend.service.AuctionProcessService;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class AuctionProcessServiceImpl implements AuctionProcessService {
    private final AuctionProcessRepository auctionProcessRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionLogService auctionLogService;

    @Override
    public AuctionProcess createAuctionProcess(AuctionProcess auctionProcess) {
        return auctionProcessRepository.save(auctionProcess);
    }

    @Override
    public AuctionProcess getAuctionProcess(Long auctionId) {
        return auctionProcessRepository
                .findAuctionProcessByAuctionId(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction with id: " + auctionId + " was not found"));
    }

    @Override
    public AuctionProcess updateAuctionProcess(long auctionId, AuctionProcess newAuctionProcess) throws ValidationException {
        AuctionProcess auctionProcess =
                auctionProcessRepository
                        .findAuctionProcessByAuctionId(auctionId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Post auction process with auction id: "
                                                        + auctionId + " was not found"));

        if (newAuctionProcess.getCurrentBank() < auctionProcess.getLot().getMinBank()
                || newAuctionProcess.getCurrentBank() < auctionProcess.getCurrentBank()
                || (newAuctionProcess.getCurrentBank() - auctionProcess.getCurrentBank())
                        < auctionProcess.getLot().getStep()) {
            throw new ValidationException("Auction Process was not valid");
        }

        auctionProcess.setCurrentBank(newAuctionProcess.getCurrentBank());
        auctionProcess.setRemainingTime(newAuctionProcess.getRemainingTime());

        auctionLogService.logBet(auctionId, auctionProcess);
        return auctionProcessRepository.save(auctionProcess);
    }

    @Override
    public String getRemainingTime(long auctionId) {
        Auction auction = auctionRepository.getById(auctionId);
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
