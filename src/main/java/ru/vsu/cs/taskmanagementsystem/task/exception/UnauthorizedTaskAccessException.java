package ru.vsu.cs.taskmanagementsystem.task.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedTaskAccessException extends RuntimeException {

    private final HttpStatus status;

    public UnauthorizedTaskAccessException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }

    public UnauthorizedTaskAccessException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}
