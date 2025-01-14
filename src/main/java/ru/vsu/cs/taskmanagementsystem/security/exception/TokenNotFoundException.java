package ru.vsu.cs.taskmanagementsystem.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public TokenNotFoundException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }

    public TokenNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}