package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.netcracker.backend.exception.bet.BankLessThanMinException;
import ru.netcracker.backend.exception.bet.BankLessThanOldException;
import ru.netcracker.backend.exception.bet.BankLessThanStepException;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.requests.BetRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.service.BetService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BetController {
    private final ModelMapper modelMapper;
    private final BetService betService;

    @MessageMapping("/play/{id}")
    @SendTo("/auction/state/{id}")
    public BetResponse play(@DestinationVariable Long id, BetRequest betRequest)
            throws BankLessThanStepException, BankLessThanMinException, BankLessThanOldException {
        BetResponse betResponse =
                modelMapper.map(
                        betService.makeBet(id, modelMapper.map(betRequest, Bet.class)),
                        BetResponse.class);

        log.info("auction with id: {} has: {}", id, betResponse);
        return betResponse;
    }

    @MessageMapping("/sync/{id}")
    @SendTo("/auction/time/remaining/{id}")
    public String getRemainingTime(@DestinationVariable Long id) {
        return betService.getRemainingTime(id);
    }
}
