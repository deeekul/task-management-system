package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;

@Builder
public record UserTaskUpdateRequest(

        @Schema(description = "Идентификатор задачи", example = "1")
        Long id,

        @Schema(description = "Статус задачи", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Комментарий к задаче")
        CommentRequest comment
) {
}
