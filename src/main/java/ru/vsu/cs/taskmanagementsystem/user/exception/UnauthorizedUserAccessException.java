package ru.vsu.cs.taskmanagementsystem.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedUserAccessException extends RuntimeException {

    private final HttpStatus status;

    public UnauthorizedUserAccessException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }

    public UnauthorizedUserAccessException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}
