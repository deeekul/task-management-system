package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;

@Builder
public record TaskUpdateRequest(

        @Size(min = 3, max = 100, message = "Название задачи должно содержать от 3 до 100 символов")
        @Schema(description = "Название задачи", example = "Разработать новую функцию поиска")
        String title,

        @Size(min = 3, max = 255, message = "Описание задачи должно содержать от 3 до 255 символов")
        @Schema(description = "Описание задачи", example = "Реализовать новый алгоритм поиска по базе данных")
        String description,

        @Schema(description = "Статус задачи", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Приоритет задачи", example = "HIGH")
        TaskPriority priority,

        @Min(value = 1, message = "Идентификатор исполнителя должен быть положительным")
        @Schema(description = "Идентификатор нового исполнителя", example = "2")
        Long assigneeId
) {
}
