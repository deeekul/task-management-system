package ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request.UserRequest;

@Builder
public record CommentRequest(

        @Schema(description = "Текст комментария", example = "Сравнить условия разных платежных систем")
        String text
) {
}
