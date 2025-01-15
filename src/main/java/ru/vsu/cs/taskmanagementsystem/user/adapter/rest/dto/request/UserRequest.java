package ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest (

        @Size(min = 3, max = 15, message = "Имя пользователя  должно содержать от 2 до 15 символов")
        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Size(min = 2, max = 15, message = "Фамилия пользователя должна содержать от 2 до 15 символов")
        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName
) {
}
