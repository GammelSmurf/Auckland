package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@CrossOrigin("*")
@RequiredArgsConstructor
public class HomeController {
    @GetMapping
    public String home() {
        return "Hello world!";
    }
}
