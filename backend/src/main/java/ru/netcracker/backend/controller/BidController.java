package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Message;
import ru.netcracker.backend.model.requests.BidRequest;
import ru.netcracker.backend.model.requests.MessageRequest;
import ru.netcracker.backend.model.responses.BidResponse;
import ru.netcracker.backend.model.responses.MessageResponse;
import ru.netcracker.backend.service.BidService;
import ru.netcracker.backend.service.MessageService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@Slf4j
@Validated
public class BidController {
    private final BidService bidService;
    private final MessageService messageService;
    private final ModelMapper modelMapper;

    @Autowired
    public BidController(BidService bidService, MessageService messageService, ModelMapper modelMapper) {
        this.bidService = bidService;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/play/{auctionId}")
    @SendTo("/auction/state/{auctionId}")
    public BidResponse play(@DestinationVariable Long auctionId, @Valid BidRequest bidRequest)
            throws ValidationException {
        BidResponse bidResponse = bidService.makeBid(auctionId, bidRequest.getAmount(), bidRequest.getUsername());

        log.info("auction with id: {} has: {}", auctionId, bidResponse);
        return bidResponse;
    }

    @MessageMapping("/send/{auctionId}")
    @SendTo("/auction/chat/{auctionId}")
    public MessageResponse send(@DestinationVariable Long auctionId, @Valid MessageRequest messageRequest)
            throws ValidationException {
        MessageResponse messageResponse = messageService.addMessage(modelMapper.map(messageRequest, Message.class));
        log.info("auction with id: {} received a message: {}", auctionId, messageResponse);
        return messageResponse;
    }
}
