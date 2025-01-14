package ru.vsu.cs.taskmanagementsystem.user.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vsu.cs.taskmanagementsystem.user.exception.InvalidPasswordException;
import ru.vsu.cs.taskmanagementsystem.user.exception.UserNotFoundException;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleException(UserNotFoundException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorMessage> handleException(InvalidPasswordException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.BAD_REQUEST);
    }
}