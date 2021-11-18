package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.Message;
import ru.netcracker.backend.requests.BetRequest;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.requests.MessageRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.LotResponse;
import ru.netcracker.backend.responses.MessageResponse;
import ru.netcracker.backend.service.MessageService;

@Controller
@Slf4j
public class MessageController {
    ModelMapper modelMapper;
    MessageService messageService;
    @Autowired
    public MessageController(ModelMapper modelMapper, MessageService messageService) {
        this.modelMapper = modelMapper;
        this.messageService = messageService;
    }

    @MessageMapping("/send/{id}")
    @SendTo("/auction/chat/{id}")
    public MessageResponse play(@DestinationVariable Long id, MessageRequest messageRequest)
            throws ValidationException {
        MessageResponse messageResponse=messageService.addMessage(modelMapper.map(messageRequest,Message.class));
        log.info("auction with id: {} received a message: {}", id, messageResponse);
        return messageResponse;
    }

}
