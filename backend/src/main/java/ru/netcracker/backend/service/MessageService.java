package ru.netcracker.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.Message;
import ru.netcracker.backend.responses.MessageResponse;

import java.util.List;

public interface MessageService {

    List<MessageResponse> getMessagesByAuction(Long auctionId);

    void deleteMessage(Long messageId);

    MessageResponse addMessage(Message message);

    void deleteOldChats();
}
