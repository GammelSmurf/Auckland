package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.AuctionLog;
import ru.netcracker.backend.repository.AuctionLogRepository;
import ru.netcracker.backend.responses.AuctionLogResponse;
import ru.netcracker.backend.service.AuctionLogService;
import ru.netcracker.backend.util.AuctionLogLevel;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionLogServiceImpl implements AuctionLogService {
    private final AuctionLogRepository auctionLogRepository;
    private final SimpMessagingTemplate template;
    private final ModelMapper modelMapper;

    private final String LOG_SEP = "\u0020";

    private final String WEB_SOCKET_PATH_TEMPLATE = "/auction/logs/%d";

    private final String LOG_BET_MSG_TEMPLATE = "%s повысил ставку до %d";
    private final String LOG_CHANGE_MSG_TEMPLATE = "Статус аукциона изменился на %s";

    private String generateMainString(AuctionLogLevel level, String msg) {
        return level.text() + LOG_SEP + msg;
    }

    private Date generateDate() {
        return new Date(System.currentTimeMillis());
    }

    private void sendObjectToWebSocket(Long auctionId, Object obj) {
        template.convertAndSend(String.format(WEB_SOCKET_PATH_TEMPLATE, auctionId), obj);
    }

    private void sendAuctionLogToWebSocket(Long auctionId, AuctionLog auctionLog) {
        sendObjectToWebSocket(auctionId, modelMapper.map(auctionLog, AuctionLogResponse.class));
    }

    private AuctionLog addAuctionBetLog(AuctionProcess auctionProcess) {
        AuctionLog auctionLog = new AuctionLog();
        auctionLog.setAuction(auctionProcess.getAuction());
        auctionLog.setLogMessage(
                generateMainString(
                        AuctionLogLevel.AUCTION_BET,
                        String.format(
                                LOG_BET_MSG_TEMPLATE,
                                auctionProcess.getUser().getUsername(),
                                auctionProcess.getCurrentBank())));
        auctionLog.setLogTime(generateDate());

        return auctionLogRepository.save(auctionLog);
    }

    private AuctionLog addAuctionStatusChangeLog(Auction auction) {
        AuctionLog auctionLog = new AuctionLog();
        auctionLog.setAuction(auction);
        auctionLog.setLogMessage(
                generateMainString(
                        AuctionLogLevel.AUCTION_STATUS_CHANGE,
                        String.format(LOG_CHANGE_MSG_TEMPLATE, auction.getStatus())));
        auctionLog.setLogTime(generateDate());

        return auctionLogRepository.save(auctionLog);
    }

    @Override
    public void logBet(Long auctionId, AuctionProcess auctionProcess) {
        AuctionLog auctionLog = addAuctionBetLog(auctionProcess);
        sendAuctionLogToWebSocket(auctionId, auctionLog);
    }

    @Override
    public void logChange(Long auctionId, Auction auction) {
        AuctionLog auctionLog = addAuctionStatusChangeLog(auction);
        sendAuctionLogToWebSocket(auctionId, auctionLog);
    }

    @Override
    public List<AuctionLog> getAuctionLogs(long auctionId) {
        return auctionLogRepository.findAuctionLogsByAuctionId(auctionId);
    }
}
