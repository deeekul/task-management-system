package ru.vsu.cs.taskmanagementsystem.task.adapter;

import org.mapstruct.Mapper;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.Task;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskCreateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response.TaskResponse;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task map(TaskCreateRequest taskRequest);

    TaskResponse map(Task task);

    Task map(TaskResponse taskResponse);
}
