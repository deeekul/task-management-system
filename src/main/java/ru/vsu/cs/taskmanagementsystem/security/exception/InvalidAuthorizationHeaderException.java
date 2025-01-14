package ru.vsu.cs.taskmanagementsystem.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidAuthorizationHeaderException extends RuntimeException {

    private final HttpStatus status;

    public InvalidAuthorizationHeaderException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }

    public InvalidAuthorizationHeaderException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}