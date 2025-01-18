package ru.vsu.cs.taskmanagementsystem.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;

@Builder
public record RegisterRequest(

        @Size(min = 3, max = 15, message = "Имя пользователя  должно содержать от 3 до 15 символов")
        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Size(min = 2, max = 15, message = "Фамилия пользователя должна содержать от 2 до 15 символов")
        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName,

        @Size(min = 3, max = 30, message = "Логин пользователя должен содержать от 3 до 30 символов")
        @Pattern(regexp = "^[a-z]{2,}+_[a-z]{1}$",
                 message = "Логин должен содержать фамилию и первую буквы имени, например, 'ivanov_i'")
        @Schema(description = "Логин пользователя", example = "ivanov_i")
        String login,

        @Size(min = 8, max = 20, message = "Пароль должен содержать от 8 до 20 символов")
        @Schema(description = "Пароль пользователя", example = "p@ssw0rd")
        String password,

        @NotNull(message = "Роль пользователя не должна быть пустой")
        @Schema(description = "Роль пользователя", example = "USER")
        Role role
) {
}