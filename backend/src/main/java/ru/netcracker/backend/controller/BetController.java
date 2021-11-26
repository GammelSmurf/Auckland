package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.requests.BetRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.service.BetService;

import javax.validation.Valid;

@Controller
@Slf4j
@Validated
public class BetController {
    private final BetService betService;

    @Autowired
    public BetController(BetService betService) {
        this.betService = betService;
    }

    @MessageMapping("/play/{id}")
    @SendTo("/auction/state/{id}")
    public BetResponse play(@DestinationVariable Long id, @Valid  BetRequest betRequest)
            throws ValidationException {
        BetResponse betResponse = betService.makeBet(betRequest.getUsername(), id, betRequest.getCurrentBank());

        log.info("auction with id: {} has: {}", id, betResponse);
        return betResponse;
    }
}
