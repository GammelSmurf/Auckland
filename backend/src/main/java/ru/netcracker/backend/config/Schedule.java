package ru.netcracker.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.netcracker.backend.service.MessageService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.TransactionService;

@Configuration
@EnableScheduling
@Slf4j
public class Schedule {
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final TransactionService transactionService;

    @Autowired
    public Schedule(MessageService messageService,
                    NotificationService notificationService,
                    TransactionService transactionService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "${Auckland.schedule.deleteMessages.cron}")
    public void checkChats() {
        messageService.deleteOldMessagesBeforeLastSevenDays();
        log.info("Old chats are removed");
    }

    @Scheduled(cron = "${Auckland.schedule.deleteNotifications.cron}")
    public void checkNotification() {
        notificationService.deleteOldNotifications();
        log.info("Old notifications are removed");
    }

    @Scheduled(cron = "${Auckland.schedule.checkTransactions.cron}")
    public void checkTransactions() {
        transactionService.deleteTransactionIfExpired();
        log.info("Check transactions for expiration");
    }
}
