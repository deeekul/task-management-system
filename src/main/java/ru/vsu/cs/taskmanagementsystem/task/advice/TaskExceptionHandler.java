package ru.vsu.cs.taskmanagementsystem.task.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vsu.cs.taskmanagementsystem.task.exception.TaskNotFoundException;
import ru.vsu.cs.taskmanagementsystem.task.exception.UnauthorizedTaskAccessException;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

@RestControllerAdvice
public class TaskExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleException(TaskNotFoundException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedTaskAccessException.class)
    public ResponseEntity<ErrorMessage> handleException(UnauthorizedTaskAccessException ex) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getStatus().value())
                .build(),
                HttpStatus.FORBIDDEN);
    }
}