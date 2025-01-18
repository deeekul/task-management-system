package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;

import java.util.List;

@Builder
public record TaskCreateRequest(

        @NotBlank(message = "Заголовок задачи не должен быть пустым")
        @Size(min = 3, max = 100, message = "Заголовок задачи должен содержать от 3 до 100 символов")
        @Schema(description = "Заголовок задачи", example = "Рефакторинг модуля авторизации")
        String title,

        @NotBlank(message = "Описание задачи не должно быть пустым")
        @Size(min = 3, max = 255, message = "Описание задачи должно содержать от 3 до 255 символов")
        @Schema(description = "Описание задачи",
                example = "Улучшить производительность и безопасность модуля авторизации")
        String description,

        @NotNull(message = "Статус задачи не должен быть пустым")
        @Schema(description = "Статус задачи", example = "PENDING")
        TaskStatus status,

        @NotNull(message = "Приоритет задачи не должен быть пустым")
        @Schema(description = "Приоритет задачи", example = "HIGH")
        TaskPriority priority,

        @NotNull(message = "Идентификатор автора не может быть пустым")
        @Schema(description = "Идентификатор автора задачи")
        Long authorId,

        @Schema(description = "Идентификатор исполнителя задачи")
        Long assigneeId,

        @Schema(description = "Комментарии к задаче")
        List<CommentRequest> comments) {
}
