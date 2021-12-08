package ru.netcracker.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.responses.NotificationResponse;
import ru.netcracker.backend.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    private final NotificationService notificationService;

    @Autowired
    public UserController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{id}")
    public List<NotificationResponse> getUserNotifications(@PathVariable(name = "id") Long userId) {
        return notificationService.getUserNotifications(userId);
    }
}
