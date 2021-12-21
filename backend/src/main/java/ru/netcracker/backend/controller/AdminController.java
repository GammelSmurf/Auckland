package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.requests.MoneyRequest;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@Slf4j
@Validated
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@NotBlank @RequestParam("username") String username) throws UserExistsException {
        UserResponse userResponse = userService.banUser(username);
        log.info("banned user: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@NotBlank @RequestParam("username") String username) {
        UserResponse userResponse = userService.unbanUser(username);
        log.info("unbanned user: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/money/add")
    public ResponseEntity<?> addMoney(@Valid @RequestBody MoneyRequest moneyRequest) {
        UserResponse userResponse = userService.addMoney(moneyRequest.getUsername(), moneyRequest.getMoney());
        log.info("added {}$ to user {}", moneyRequest.getMoney(), moneyRequest.getUsername());
        return ResponseEntity.ok(userResponse);
    }
}
