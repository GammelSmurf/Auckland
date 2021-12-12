package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.exception.user.UserExistsException;
import ru.netcracker.backend.model.requests.CurrencyRequest;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.service.CategoryService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@Slf4j
@Validated
public class AdminController {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;

    @Autowired
    public AdminController(ModelMapper modelMapper, UserService userService, NotificationService notificationService, CategoryService categoryService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.notificationService = notificationService;
        this.categoryService = categoryService;
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

    @PostMapping("/category/add")
    public ResponseEntity<?> addCategory(@NotBlank @Size(min = 3, max = 255) @RequestBody String categoryName) {
        CategoryResponse categoryResponse=categoryService.addCategory(categoryName);
        log.info("added category with name {}",categoryName);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }
}
