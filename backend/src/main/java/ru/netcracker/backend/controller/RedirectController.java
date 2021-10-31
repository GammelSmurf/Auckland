package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.netcracker.backend.service.AuthService;

@Controller
@RequiredArgsConstructor
public class RedirectController implements ErrorController {
    private final AuthService authService;
    private static final String PATH = "/error";

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@Param("code") String code) {
        if (authService.verify(code)) {
            return ResponseEntity.ok("User verified");
        } else {
            return ResponseEntity.badRequest().body("User not verified");
        }
    }

    @RequestMapping(value = PATH)
    @ResponseStatus(HttpStatus.OK)
    public String error() {
        return "forward:/index.html";
    }
}
