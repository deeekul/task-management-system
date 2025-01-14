package ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;

@Builder
public record UserResponse(

        @Schema(description = "Идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName,

        @Schema(description = "Логин пользователя", example = "ivanov_i")
        String login,

        @Schema(description = "Роль пользователя", example = "USER")
        Role role
) {
}