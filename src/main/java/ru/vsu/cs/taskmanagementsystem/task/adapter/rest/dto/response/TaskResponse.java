package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.response.CommentResponse;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;

import java.util.List;


@Builder
public record TaskResponse(

        @Schema(description = "Идентификатор задачи", example = "1")
        Long id,

        @Schema(description = "Заголовок задачи", example = "Рефакторинг модуля авторизации")
        String title,

        @Schema(description = "Описание задачи",
                example = "Улучшить производительность и безопасность модуля авторизации")
        String description,

        @Schema(description = "Статус задачи", example = "PENDING")
        TaskStatus status,

        @Schema(description = "Приоритет задачи", example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Автор задачи")
        UserResponse author,

        @Schema(description = "Исполнитель задачи")
        UserResponse assignee,

        @Schema(description = "Комментарии к задаче")
        List<CommentResponse> comments
) {
}

