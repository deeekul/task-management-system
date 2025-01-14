package ru.vsu.cs.taskmanagementsystem.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthenticationRequest(

        @Size(min = 3, max = 30, message = "Логин пользователя должен содержать от 3 до 30 символов")
        @Pattern(regexp = "^[a-z]{2,}+_[a-z]{1}$",
                 message = "Логин должен содержать фамилию и первую буквы имени, например, 'ivanov_i'")
        String login,

        @Size(min = 8, max = 20, message = "Пароль должен содержать от 8 до 20 символов")
        String password
) {
}