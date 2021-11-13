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

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

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
            @Param("code") String code, @Param("username") String username) {
        if (authService.verify(code, username)) {
            return ResponseEntity.ok("User verified");
        } else {
            return ResponseEntity.badRequest().body("User not verified");
        }
    }

    @GetMapping("/recover")
    public ResponseEntity<Void> recoverPassword(
            @Param("code") String code, @Param("username") String username)
            throws MessagingException, UnsupportedEncodingException {
        authService.generateNewPassword(code, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = PATH)
    @ResponseStatus(HttpStatus.OK)
    public String error() {
        return "forward:/index.html";
    }
}
