package ru.netcracker.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
public class RedirectController implements ErrorController {
    private final AuthService authService;
    private static final String PATH = "/error";

    @Autowired
    public RedirectController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(
            @Param("username") String username, @Param("code") String code) {
        if (authService.verify(username, code)) {
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
