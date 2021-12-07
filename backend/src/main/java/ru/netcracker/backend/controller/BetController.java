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
import ru.netcracker.backend.model.Message;
import ru.netcracker.backend.requests.BetRequest;
import ru.netcracker.backend.requests.MessageRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.MessageResponse;
import ru.netcracker.backend.service.BetService;
import ru.netcracker.backend.service.MessageService;

import javax.validation.Valid;

@Controller
@Slf4j
@Validated
public class BetController {
    private final BetService betService;
    private final MessageService messageService;
    private final ModelMapper modelMapper;

    @Autowired
    public BetController(BetService betService, MessageService messageService, ModelMapper modelMapper) {
        this.betService = betService;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/play/{id}")
    @SendTo("/auction/state/{id}")
    public BetResponse play(@DestinationVariable Long id, @Valid  BetRequest betRequest)
            throws ValidationException {
        BetResponse betResponse = betService.makeBet(betRequest.getUsername(), id, betRequest.getCurrentBank());

        log.info("auction with id: {} has: {}", id, betResponse);
        return betResponse;
    }

    @MessageMapping("/send/{id}")
    @SendTo("/auction/chat/{id}")
    public MessageResponse play(@DestinationVariable Long id, @Valid MessageRequest messageRequest)
            throws ValidationException {
        MessageResponse messageResponse=messageService.addMessage(modelMapper.map(messageRequest, Message.class));
        log.info("auction with id: {} received a message: {}", id, messageResponse);
        return messageResponse;
    }
}
