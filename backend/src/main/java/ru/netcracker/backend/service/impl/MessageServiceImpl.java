package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Message;
import ru.netcracker.backend.repository.MessageRepository;
import ru.netcracker.backend.model.responses.MessageResponse;
import ru.netcracker.backend.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, ModelMapper modelMapper){
        this.messageRepository=messageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MessageResponse> getMessagesByAuctionId(Long auctionId) {
        return messageRepository
                .getMessagesByAuction_Id(auctionId).stream()
                .map(message -> modelMapper.map(message,MessageResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse addMessage(Message message) {
        return modelMapper.map(messageRepository.save(message), MessageResponse.class);
    }

    @Override
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public void deleteOldMessagesBeforeLastSevenDays() {
        messageRepository.deleteOldMessagesBefore(LocalDateTime.now().minusDays(7));
    }
}
