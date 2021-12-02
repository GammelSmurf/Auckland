package ru.netcracker.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.netcracker.backend.service.MessageService;

@Configuration
@EnableScheduling
@Slf4j
public class Schedule {
    public static int i=0;
    private final MessageService messageService;

    @Autowired
    public Schedule(MessageService messageService){
        this.messageService=messageService;
    }

    @Scheduled(cron = "0 0 05 * * ?")
    public void checkChats(){
        messageService.deleteOldChats();
        log.info("Old chats are removed");
    }
}
