package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.responses.MessageResponse;
import ru.netcracker.backend.service.MessageService;
import java.util.List;

@RestController
@RequestMapping("/api/message")
@CrossOrigin("*")
@Slf4j
@Validated
public class MessageController {
    MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public List<MessageResponse> getMessagesByAuction(@PathVariable(name = "id") Long id) {
        return messageService.getMessagesByAuction(id);
    }

}
