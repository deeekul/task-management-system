package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;

@Builder
public record TaskUpdateRequest(

        @Size(min = 3, max = 100)
        @Schema(description = "Название задачи", example = "Разработать новую функцию поиска")
        String title,

        @Size(min = 3, max = 255)
        @Schema(description = "Описание задачи", example = "Реализовать новый алгоритм поиска по базе данных")
        String description,

        @Schema(description = "Статус задачи", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Приоритет задачи", example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Идентификатор нового исполнителя", example = "2")
        Long assigneeId
) {
}
