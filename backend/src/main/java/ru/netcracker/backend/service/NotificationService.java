package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.responses.NotificationResponse;
import ru.netcracker.backend.util.enumiration.NotificationLevel;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(Long userId);

    void log(NotificationLevel level, User user, Auction auction);
}
