package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Log;
import ru.netcracker.backend.repository.LogRepository;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.util.LogLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;
    private final SimpMessagingTemplate template;
    private final ModelMapper modelMapper;

    private static final String WEB_SOCKET_PATH_TEMPLATE = "/auction/logs/%d";
    private static final String LOG_BET_MSG_TEMPLATE = "%s повысил ставку до %s";
    private static final String LOG_CHANGE_MSG_TEMPLATE = "Статус аукциона изменился на %s";
    private static final String LOG_WINNER_MSG_TEMPLATE = "%s выиграл лот \"%s\"";
    private static final String LOG_NO_WINNER_MSG_TEMPLATE = "Никто не выиграл лот \"%s\"";
    private static final String LOG_SEP = "\u0020";

    @Autowired
    public LogServiceImpl(LogRepository logRepository, SimpMessagingTemplate template, ModelMapper modelMapper) {
        this.logRepository = logRepository;
        this.template = template;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LogResponse> getAuctionLogs(Long auctionId) {
        return logRepository.findAllByAuction_Id(auctionId).stream()
                .map(log -> modelMapper.map(log, LogResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void log(LogLevel level, Auction auction) {
        switch (level) {
            case AUCTION_BET:
                sendAuctionLogToWs(
                        auction.getId(),
                        addLog(level, auction,
                                String.format(LOG_BET_MSG_TEMPLATE, auction.getBet().getUser().getUsername(), auction.getBet().getCurrentBank().toPlainString())));
                break;
            case AUCTION_STATUS_CHANGE:
                sendAuctionLogToWs(
                        auction.getId(),
                        addLog(level, auction, String.format(LOG_CHANGE_MSG_TEMPLATE, auction.getStatus())));
                break;
            case AUCTION_WINNER:
                sendAuctionLogToWs(
                        auction.getId(),
                        addLog(level, auction,
                                String.format(LOG_WINNER_MSG_TEMPLATE, auction.getCurrentLot().getWinner().getUsername(), auction.getCurrentLot().getName())));
                break;
            case AUCTION_NO_WINNER:
                sendAuctionLogToWs(auction.getId(),
                        addLog(level, auction,
                                String.format(LOG_NO_WINNER_MSG_TEMPLATE, auction.getCurrentLot().getName())));
                break;
        }
    }

    private Log addLog(LogLevel level, Auction auction, String msg) {
        Log log = new Log();
        log.setAuction(auction);
        log.setLogMessage(generateMainString(level, msg));
        log.setLogTime(LocalDateTime.now());

        return logRepository.save(log);
    }

    private void sendAuctionLogToWs(Long auctionId, Log log) {
        sendObjectToWs(auctionId, modelMapper.map(log, LogResponse.class));
    }

    private void sendObjectToWs(Long auctionId, Object obj) {
        template.convertAndSend(String.format(WEB_SOCKET_PATH_TEMPLATE, auctionId), obj);
    }

    private String generateMainString(LogLevel level, String msg) {
        return level.text() + LOG_SEP + msg;
    }
}
