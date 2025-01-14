package ru.vsu.cs.taskmanagementsystem.security.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.AuthenticationRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.RegisterRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.response.AuthenticationResponse;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Tag(name = "User Authentication API", description = "API для регистрации и аутентификации пользователей")
public interface AuthenticationApi {

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная регистрация пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "В теле запроса указаны некорректные поля/значения " +
                                    "или отсутствуют обязательные для заполнения",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    }
            )
    })
    @Operation(summary = "Регистрация нового пользователя")
    ResponseEntity<?> register(
            @RequestBody(description = "Параметры для регистрации пользователя") RegisterRequest request,
            BindingResult bindingResult);

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthenticationResponse.class)
                            )
                    }
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
    @Operation(summary = "Аутентификация пользователя")
    ResponseEntity<?> authenticate(
            @RequestBody(description = "Параметры для аутентификации пользователя") AuthenticationRequest request,
            BindingResult bindingResult) throws AuthenticationException;

    @ApiResponse(
            responseCode = "200",
            description = "Успешное обновление токена доступа"
    )
    @Operation(summary = "Обновление токена доступа")
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}