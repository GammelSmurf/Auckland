package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.Message;
import ru.netcracker.backend.model.responses.MessageResponse;

import java.util.List;

public interface MessageService {

    List<MessageResponse> getMessagesByAuction(Long auctionId);

    void deleteMessage(Long messageId);

    MessageResponse addMessage(Message message);

    void deleteOldChats();
}
