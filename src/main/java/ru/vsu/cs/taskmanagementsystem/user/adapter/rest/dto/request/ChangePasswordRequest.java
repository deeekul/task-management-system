package ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Текущий пароль не должен быть пустым")
        @JsonProperty("old_password")
        @Schema(description = "Текущий пароль", example = "p@ssw0rd")
        String oldPassword,

        @Size(min = 8, max = 20, message = "Новый пароль должен содержать от 8 до 20 символов")
        @JsonProperty("new_password")
        @Schema(description = "Пароль для подтверждения", example = "new_p@ssw0rd")
        String newPassword,

        @Size(min = 8, max = 20, message = "Пароль для подтверждения должен содержать от 8 до 20 символов")
        @JsonProperty("confirmation_password")
        @Schema(description = "Пароль для подтверждения", example = "new_p@ssw0rd")
        String confirmationPassword
) {
}