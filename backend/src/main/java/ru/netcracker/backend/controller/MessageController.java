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
import ru.netcracker.backend.requests.MessageRequest;
import ru.netcracker.backend.responses.MessageResponse;
import ru.netcracker.backend.service.MessageService;

import javax.validation.Valid;

@Controller
@Slf4j
@Validated
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
    public MessageResponse play(@DestinationVariable Long id,@Valid MessageRequest messageRequest)
            throws ValidationException {
        MessageResponse messageResponse=messageService.addMessage(modelMapper.map(messageRequest,Message.class));
        log.info("auction with id: {} received a message: {}", id, messageResponse);
        return messageResponse;
    }

}
