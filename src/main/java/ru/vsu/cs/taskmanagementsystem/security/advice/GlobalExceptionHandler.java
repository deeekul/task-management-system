package ru.vsu.cs.taskmanagementsystem.security.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vsu.cs.taskmanagementsystem.security.exception.InvalidAuthorizationHeaderException;
import ru.vsu.cs.taskmanagementsystem.security.exception.TokenNotFoundException;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleException(TokenNotFoundException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ErrorMessage> handleException(InvalidAuthorizationHeaderException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.BAD_REQUEST);
    }
}