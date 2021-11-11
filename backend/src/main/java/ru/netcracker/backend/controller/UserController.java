package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.UserExistsException;
import ru.netcracker.backend.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@RequestParam("username") String username) throws UserExistsException {
        return new ResponseEntity<>(userService.banUser(username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@RequestParam("username") String username) {
        return new ResponseEntity<>(userService.unbanUser(username), HttpStatus.OK);
    }
}
