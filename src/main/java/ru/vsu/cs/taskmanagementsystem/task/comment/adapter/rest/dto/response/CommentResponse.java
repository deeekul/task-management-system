package ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(

        @Schema(description = "Идентификатор комментария", example = "1")
        Long id,

        @Schema(description = "Текст комментария", example = "Сравнить условия разных платежных систем.")
        String text,

        @Schema(description = "Дата и время комментария", example = "2025-01-15T12:30:00")
        LocalDateTime createdDate,

        @Schema(description = "Автор комментария")
        UserResponse user
) {
}
