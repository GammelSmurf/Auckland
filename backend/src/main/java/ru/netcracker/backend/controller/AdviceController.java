package ru.netcracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netcracker.backend.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class AdviceController {
    @ExceptionHandler({ValidationException.class, BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(generateExceptionReturnBody(ex));
    }

    private Map<String, Object> generateExceptionReturnBody(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return body;
    }
}
