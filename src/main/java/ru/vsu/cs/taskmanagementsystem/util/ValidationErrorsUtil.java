package ru.vsu.cs.taskmanagementsystem.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ValidationErrorsUtil {

    public static String returnErrorsToClient(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();

            errorMsg.append(fieldName).append(" - ").append(message).append("; ");
        }
        if (!errorMsg.isEmpty()) {
            errorMsg.delete(errorMsg.length() - 2, errorMsg.length());
        }

        return errorMsg.toString();
    }
}