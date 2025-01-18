package ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequest(

        @NotBlank(message = "Текст комментария не должен быть пустым")
        @Size(min = 3, max = 100, message = "Текст комментария должен содержать от 3 до 100 символов")
        @Schema(description = "Текст комментария", example = "Сравнить условия разных платежных систем")
        String text
) {
}
