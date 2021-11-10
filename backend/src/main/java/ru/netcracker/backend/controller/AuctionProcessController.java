package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.requests.AuctionProcessRequest;
import ru.netcracker.backend.responses.AuctionProcessResponse;
import ru.netcracker.backend.service.AuctionProcessService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuctionProcessController {
    private final ModelMapper modelMapper;
    private final AuctionProcessService auctionProcessService;

    @MessageMapping("/play/{id}")
    @SendTo("/auction/state/{id}")
    public AuctionProcessResponse play(
            @DestinationVariable Long id, AuctionProcessRequest auctionProcessRequest)
            throws ValidationException {
        AuctionProcessResponse auctionProcessResponse =
                modelMapper.map(
                        auctionProcessService.updateAuctionProcess(
                                id, modelMapper.map(auctionProcessRequest, AuctionProcess.class)),
                        AuctionProcessResponse.class);

        log.info("auction with id: {} has: {}", id, auctionProcessResponse);
        return auctionProcessResponse;
    }

    @MessageMapping("/sync/{id}")
    @SendTo("/auction/time/remaining/{id}")
    public String getRemainingTime(@DestinationVariable Long id) {
        return auctionProcessService.getRemainingTime(id);
    }
}
