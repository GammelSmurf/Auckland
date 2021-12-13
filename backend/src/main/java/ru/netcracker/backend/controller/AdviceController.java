package ru.netcracker.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netcracker.backend.exception.ValidationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler({ValidationException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        return new ResponseEntity<>(generateExceptionReturnBody(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledException() {
        return new ResponseEntity<>(generateExceptionReturnBodyWithCustomMessage("The user is not confirmed. Please, check your email."), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> generateExceptionReturnBody(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return body;
    }

    private Map<String, Object> generateExceptionReturnBodyWithCustomMessage(String customMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", customMessage);
        return body;
    }
}
