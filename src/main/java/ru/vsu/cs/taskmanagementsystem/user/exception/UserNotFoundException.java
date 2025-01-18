package ru.vsu.cs.taskmanagementsystem.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public UserNotFoundException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }

    public UserNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}