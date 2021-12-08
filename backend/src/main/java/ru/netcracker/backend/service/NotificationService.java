package ru.netcracker.backend.service;

import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.User;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.responses.NotificationResponse;
import ru.netcracker.backend.util.NotificationLevel;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(Long userId);

    void log(NotificationLevel level, User user, Auction auction);
}
