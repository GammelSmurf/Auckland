package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.auction.AuctionStatus;
import ru.netcracker.backend.repository.AuctionProcessRepository;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.service.AuctionProcessService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class AuctionProcessServiceImpl implements AuctionProcessService {
    private final AuctionProcessRepository auctionProcessRepository;
    private final AuctionRepository auctionRepository;

    @Override
    public AuctionProcess createAuctionProcess(AuctionProcess auctionProcess) {
        return auctionProcessRepository.save(auctionProcess);
    }

    @Override
    public AuctionProcess getAuctionProcess(Long auctionId) {
        return auctionProcessRepository
                .findAuctionProcessByAuction(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Post id: " + auctionId));
    }

    @Override
    public AuctionProcess updateAuctionProcess(long auctionId, AuctionProcess auctionProcess) {
        AuctionProcess process =
                auctionProcessRepository
                        .findAuctionProcessByAuction(auctionId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                                "Post auction process with auction id: "
                                                        + auctionId));

        process.setCurrentBank(auctionProcess.getCurrentBank());
        process.setRemainingTime(auctionProcess.getRemainingTime());

        return auctionProcessRepository.save(process);
    }

    @Override
    public String getRemainingTime(Long id) {
        Auction auction = auctionRepository.getById(id);
        Timestamp beginDate = auction.getBeginDate();
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());

        if (beginDate.after(currentDate)) {
            return new SimpleDateFormat("HH:mm:ss")
                    .format(new Timestamp(beginDate.getTime() - currentDate.getTime()));
        }
        auction.setStatus(AuctionStatus.RUNNING);
        auctionRepository.save(auction);

        return "00:00:00";
    }
}
