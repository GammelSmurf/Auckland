package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.responses.NotificationResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.model.requests.UserRequest;
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
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(NotificationService notificationService, UserService userService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("/notifications/{id}")
    public List<NotificationResponse> getUserNotifications(@PathVariable(name = "id") Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse=userService.updateUser(userRequest);
        log.info("updated user: {} with username: {}", userRequest, userRequest.getUsername());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
