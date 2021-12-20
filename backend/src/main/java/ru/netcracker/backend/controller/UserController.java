package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.entity.Message;
import ru.netcracker.backend.model.requests.MessageRequest;
import ru.netcracker.backend.model.responses.MessageResponse;
import ru.netcracker.backend.model.responses.NotificationResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.model.requests.UserRequest;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@Slf4j
public class UserController {
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public UserController(NotificationService notificationService, UserService userService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> getUserNotifications() {
        return notificationService.getUserNotifications();
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.updateUser(userRequest);
        log.info("updated user: {} with username: {}", userRequest, userRequest.getUsername());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @MessageMapping("/balance/{username}")
    @SendTo("/auction/balance/{username}")
    public BigDecimal sendToWsUserBalance(@DestinationVariable String username) {
        return userService.getMoneyByUsername(username);
    }
}
