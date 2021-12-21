package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Notification;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.responses.NotificationResponse;
import ru.netcracker.backend.repository.NotificationRepository;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.util.SecurityUtil;
import ru.netcracker.backend.util.enumiration.NotificationLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate template;
    private final ModelMapper modelMapper;
    private final NotificationRepository notificationRepository;

    private static final String WEB_SOCKET_PATH_TEMPLATE = "/user/notifications/%d";

    private static final String USER_SUBSCRIBED_MSG_TEMPLATE = "User '%s' subscribed to auction '%s'";
    private static final String SUBSCRIBED_AUCTION_STATUS_CHANGED_MSG_TEMPLATE = "An auction you subscribed '%s' changed status to '%s'";
    private static final String OWN_AUCTION_STATUS_CHANGED_MSG_TEMPLATE = "Your auction '%s' changed status to '%s'";

    @Value("${Auckland.removalPeriodInDays.notifications}")
    private Long daysForDelete;

    public NotificationServiceImpl(SimpMessagingTemplate template,
                                   ModelMapper modelMapper,
                                   NotificationRepository notificationRepository) {
        this.template = template;
        this.modelMapper = modelMapper;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationResponse> getUserNotifications() {
        return notificationRepository.findAllByUser_Username(SecurityUtil.getUsernameFromSecurityCtx()).stream()
                .map(notification -> modelMapper.map(notification, NotificationResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void log(NotificationLevel level, User user, Auction auction) {
        switch (level) {
            case USER_SUBSCRIBED:
                sendNotificationToWs(auction.getCreator().getId(),
                        addNotification(auction.getCreator(), auction, String.format(
                                USER_SUBSCRIBED_MSG_TEMPLATE,
                                user.getUsername(),
                                auction.getName())));
                break;
            case SUBSCRIBED_AUCTION_STATUS_CHANGED:
                sendNotificationToWs(auction.getCreator().getId(),
                        addNotification(auction.getCreator(), auction, String.format(
                                OWN_AUCTION_STATUS_CHANGED_MSG_TEMPLATE,
                                auction.getName(),
                                auction.getStatus())));

                for (User member : auction.getSubscribedUsers()) {
                    sendNotificationToWs(member.getId(),
                            addNotification(member, auction, String.format(
                                    SUBSCRIBED_AUCTION_STATUS_CHANGED_MSG_TEMPLATE,
                                    auction.getName(),
                                    auction.getStatus())));
                }
                break;
        }
    }

    @Override
    @Transactional
    public void deleteOldNotifications() {
        notificationRepository.deleteAllByDateTimeIsLessThan(LocalDateTime.now().minusDays(daysForDelete));
    }

    private Notification addNotification(User user, Auction auction, String msg) {
        return notificationRepository.save(Notification.builder()
                .user(user)
                .auction(auction)
                .message(msg)
                .dateTime(LocalDateTime.now())
                .build());
    }

    private void sendNotificationToWs(Long userId, Notification notification) {
        sendObjectToWs(userId, modelMapper.map(notification, NotificationResponse.class));
    }

    private void sendObjectToWs(Long userId, Object obj) {
        template.convertAndSend(String.format(WEB_SOCKET_PATH_TEMPLATE, userId), obj);
    }
}
