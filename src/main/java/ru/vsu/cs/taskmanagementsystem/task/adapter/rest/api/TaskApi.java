package ru.vsu.cs.taskmanagementsystem.task.adapter.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.AdminTaskUpdateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskCreateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.UserTaskUpdateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response.TaskResponse;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

import java.security.Principal;

@Tag(name = "Task API", description = "API для работы с задачами")
public interface TaskApi {

    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение списка задач",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            }
    )
    @Operation(summary = "Получить все задачи")
    @Parameters({
            @Parameter(name = "pageNumber", description = "Номер страницы", example = "0"),
            @Parameter(name = "pageSize", description = "Размер страницы", example = "10"),
            @Parameter(name = "status", description = "Статус задачи (необязательно)", example = "IN_PROGRESS"),
            @Parameter(name = "priority", description = "Приоритет задачи (необязательно)", example = "HIGH")
    })
    ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача по указанному идентификатору не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить задачу по идентификатору")
    ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Идентификатор пользователя") Long id,
            Principal connectedUser);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача по указанному заголовку не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить задачу по названию заголовка")
    ResponseEntity<TaskResponse> getTaskByTitle(
            @Parameter(description = "Заголовок задачи", required = true)
            @RequestParam(value = "title") String title);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка задач, созданных указанным автором",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задачи, созданные указанным автором, не найдены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить все задачи конкретного автора по идентификатору")
    @Parameters({
            @Parameter(name = "id", description = "Идентификатор автора", example = "1"),
            @Parameter(name = "pageNumber", description = "Номер страницы", example = "0"),
            @Parameter(name = "pageSize", description = "Размер страницы", example = "10"),
            @Parameter(name = "status", description = "Статус задачи (необязательно)", example = "IN_PROGRESS"),
            @Parameter(name = "priority", description = "Приоритет задачи (необязательно)", example = "HIGH")
    })
    ResponseEntity<Page<TaskResponse>> getAllTasksByAuthorId(
            @PathVariable("id") Long id,
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            Principal connectedUser
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка задач для указанного исполнителя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задачи, исполнителем которых является указанный пользователь, не найдены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить все задачи конкретного исполнителя по идентификатору")
    @Parameters({
            @Parameter(name = "id", description = "Идентификатор исполнителя", example = "1"),
            @Parameter(name = "pageNumber", description = "Номер страницы", example = "0"),
            @Parameter(name = "pageSize", description = "Размер страницы", example = "10"),
            @Parameter(name = "status", description = "Статус задачи (необязательно)", example = "IN_PROGRESS"),
            @Parameter(name = "priority", description = "Приоритет задачи (необязательно)", example = "HIGH")
    })
    ResponseEntity<Page<TaskResponse>> getAllTasksByAssigneeId(
            @PathVariable("id") Long id,
            @RequestParam(value = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            Principal connectedUser
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Успешное добавление новой задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "В теле запроса указаны недопустимые поля " +
                            "или отсутствуют обязательные для заполнения",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Создать новую задачу")
    ResponseEntity<?> createTask(
            @RequestBody(description = "Параметры для создания задачи") @Valid TaskCreateRequest taskCreateRequest,
            BindingResult bindingResult,
            Principal connectedUser);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление комментария к задаче",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "В теле запроса указаны недопустимые поля " +
                            "или отсутствуют обязательные для заполнения",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Добавить новый комментарий к задаче")
    ResponseEntity<?> addCommentToTask(
            @Parameter(description = "Идентификатор задачи") Long id,
            @RequestBody(description = "Параметры для добавления комментария к задаче")
            @Valid CommentRequest commentRequest,
            BindingResult bindingResult,
            Principal connectedUser
    );

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача по указанному идентификатору не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Обновить задачу пользователем по идентификатору")
    ResponseEntity<?> updateUserTaskById(
            @Parameter(description = "Идентификатор задачи") Long id,
            @RequestBody(description = "Параметры для обновления задачи")
            @Valid UserTaskUpdateRequest request,
            BindingResult bindingResult,
            Principal connectedUser);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача по указанному идентификатору не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Обновить задачу администратором по идентификатору")
    ResponseEntity<?> updateAdminTaskById(
            @Parameter(description = "Идентификатор задачи") Long id,
            @RequestBody(description = "Параметры для обновления задачи")
            @Valid AdminTaskUpdateRequest request,
            BindingResult bindingResult,
            Principal connectedUser);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление задачи",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача по указанному идентификатору не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Удалить задачи по идентификатору")
    ResponseEntity<Void> deleteTaskById(@Parameter(description = "Идентификатор задачи") Long id);
}