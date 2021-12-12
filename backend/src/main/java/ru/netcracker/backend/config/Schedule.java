package ru.netcracker.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.netcracker.backend.service.MessageService;
import ru.netcracker.backend.service.NotificationService;

@Configuration
@EnableScheduling
@Slf4j
public class Schedule {
    private final MessageService messageService;
    private final NotificationService notificationService;

    @Autowired
    public Schedule(MessageService messageService, NotificationService notificationService){
        this.messageService=messageService;
        this.notificationService=notificationService;
    }

    @Scheduled(cron = "${Auckland.schedule.deleteMessages.cron}")
    public void checkChats(){
        messageService.deleteOldChats();
        log.info("Old chats are removed");
    }

    @Scheduled(cron = "${Auckland.schedule.deleteNotifications.cron}")
    public void checkNotification(){
        notificationService.deleteOldNotifications();
        log.info("Old notifications are removed");
    }
}
