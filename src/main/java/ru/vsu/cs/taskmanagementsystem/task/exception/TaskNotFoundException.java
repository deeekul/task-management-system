package ru.vsu.cs.taskmanagementsystem.task.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TaskNotFoundException extends RuntimeException{

    private final HttpStatus status;

    public TaskNotFoundException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }

    public TaskNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}
