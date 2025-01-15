package ru.vsu.cs.taskmanagementsystem.task.adapter.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.taskmanagementsystem.task.TaskService;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.api.TaskApi;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskCreateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskUpdateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response.TaskResponse;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;
import java.security.Principal;

import static org.springframework.http.ResponseEntity.ok;
import static ru.vsu.cs.taskmanagementsystem.util.ValidationErrorsUtil.returnErrorsToClient;

@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@RestController
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority
    ) {
        var pageRequest = PageRequest.of(pageNumber, pageSize);
        return ok(taskService.getAllTasks(status, priority, pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") Long id) {
        return ok(taskService.getTaskById(id));
    }

    @GetMapping("/title")
    public ResponseEntity<TaskResponse> getTaskByTitle(@RequestParam(value = "title") String title) {
        return ok(taskService.getTaskByTitle(title));
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<Page<TaskResponse>> getAllTasksByAuthorId(
            @PathVariable("id") Long id,
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority
    ) {
        var pageRequest = PageRequest.of(pageNumber, pageSize);
        return ok(taskService.getAllTasksByAuthorId(id, status, priority, pageRequest));
    }

    @GetMapping("/assignee/{id}")
    public ResponseEntity<Page<TaskResponse>> getAllTasksByAssigneeId(
            @PathVariable("id") Long id,
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority
    ) {
        var pageRequest = PageRequest.of(pageNumber, pageSize);
        return ok(taskService.getAllTasksByAssigneeId(id, status, priority, pageRequest));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskCreateRequest taskCreateRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(taskCreateRequest));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addCommentToTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid CommentRequest commentRequest,
            BindingResult bindingResult,
            Principal connectedUser
    ) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        return ok(taskService.addCommentToTask(id, commentRequest, connectedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaskById(@PathVariable(("id")) Long id,
                                            @RequestBody @Valid TaskUpdateRequest taskUpdateRequest,
                                            BindingResult bindingResult,
                                            Principal connectedUser) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        return ok(taskService.updateTaskById(id, taskUpdateRequest, connectedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable("id") Long id) {
        taskService.deleteTaskById(id);
        return ok().build();
    }
}
