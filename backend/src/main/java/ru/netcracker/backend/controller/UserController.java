package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.requests.UserRequest;
import ru.netcracker.backend.model.responses.ContactInfoResponse;
import ru.netcracker.backend.model.responses.NotificationResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@Slf4j
public class UserController {
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public UserController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public UserResponse getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> getUserNotifications() {
        return notificationService.getUserNotifications();
    }

    @GetMapping("/contact")
    public ContactInfoResponse getUserContactInfoByLotId(@RequestParam("lotId") Long lotId) {
        return userService.getContactInfoByLotId(lotId);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.updateUser(userRequest);
        log.info("updated user: {} with username: {}", userRequest, userRequest.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @MessageMapping("/balance/{username}")
    public void sendToWsUserBalance(@DestinationVariable String username) {
        userService.sendMoneyToWsByUsername(username);
    }
}
