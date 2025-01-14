package ru.vsu.cs.taskmanagementsystem.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPasswordException extends RuntimeException{

    private final HttpStatus status;

    public InvalidPasswordException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
    }

    public InvalidPasswordException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
}