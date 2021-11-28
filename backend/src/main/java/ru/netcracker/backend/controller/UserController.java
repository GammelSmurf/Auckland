package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.requests.CurrencyRequest;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@Slf4j
@Validated
public class UserController {
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Autowired
    public UserController(ModelMapper modelMapper, UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@NotBlank @RequestParam("username") String username)
            throws UserExistsException {
        UserResponse userResponse = userService.banUser(username);
        log.info("banned user: {}", userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@NotBlank @RequestParam("username") String username) {
        UserResponse userResponse = userService.unbanUser(username);
        log.info("unbanned user: {}", userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping("/currency/add")
    public ResponseEntity<?> addCurrency(@Valid @RequestBody CurrencyRequest currencyRequest) {
        UserResponse userResponse = userService.addCurrency(currencyRequest.getUsername(), currencyRequest.getCurrency());
        log.info("added {}$ to user {}", currencyRequest.getCurrency(), currencyRequest.getUsername());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
