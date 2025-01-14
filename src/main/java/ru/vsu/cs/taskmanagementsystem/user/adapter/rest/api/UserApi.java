package ru.vsu.cs.taskmanagementsystem.user.adapter.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request.ChangePasswordRequest;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

import java.security.Principal;
import java.util.List;

@Tag(name = "User API", description = "API для работы с пользователями")
public interface UserApi {

    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение всех пользователей",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))
                    )
        }
    )
    @Operation(summary = "Получить всех пользователей")
    ResponseEntity<List<UserResponse>> getAllUsers();

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь по указанному идентификатору не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить пользователя по идентификатору")
    ResponseEntity<UserResponse> getUserById(@Parameter(description = "Идентификатор пользователя") Long id);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение пользователя по логину",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь по указанному логину не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Получить пользователя по логину")
    ResponseEntity<UserResponse> getUserByLogin(
            @Parameter(description = "Логин пользователя", required = true)
            @RequestParam(value = "login") String login);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление пользователя по идентификатору"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь по указанному идентификатору не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Удалить пользователя по идентификатору")
    ResponseEntity<Void> deleteUserById(@Parameter(description = "Идентификатор пользователя") Long id);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление пароля"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "В теле запроса указаны некорректные поля/значения " +
                                    "или отсутствуют обязательные поля для заполнения",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Изменить пароль пользователя")
    ResponseEntity<?> changePassword(
            @RequestBody(description = "Параметры для изменения пароля") ChangePasswordRequest request,
            BindingResult bindingResult,
            Principal connectedUser);
}