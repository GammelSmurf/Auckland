package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.model.user.User;
import ru.netcracker.backend.requests.CurrencyRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers(){
        return userService.getUsers().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@RequestParam("username") String username)
            throws UserExistsException {
        User user = userService.banUser(username);
        log.info("banned user: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@RequestParam("username") String username) {
        User user = userService.unbanUser(username);
        log.info("unbanned user: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/currency/add")
    public ResponseEntity<?> addCurrency(@RequestBody CurrencyRequest currencyRequest) {
        User user = userService.addCurrency(currencyRequest.getUsername(), currencyRequest.getCurrency());
        log.info("added {}$ to user {}",currencyRequest.getCurrency(), currencyRequest.getUsername());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
