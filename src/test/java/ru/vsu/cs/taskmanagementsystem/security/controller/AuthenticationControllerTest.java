package ru.vsu.cs.taskmanagementsystem.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.AuthenticationRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.RegisterRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.response.AuthenticationResponse;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.TokenRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.USER;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationControllerTest {

    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    void register_whenValidRegisterRequest_thenStatusOk() throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "p@ssw0rd",
                USER
        );
        var jsonRequest = objectMapper.writeValueAsString(registerRequest);

        // when
        ResultActions response = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        // then
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.length()").value(5));

        assertTrue(userRepository.findByLogin("ivanov_i").isPresent());
    }

    @Test
    void register_whenInvalidRegisterRequest_thenStatusBadRequest() throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "pass",
                USER
        );
        var jsonRequest = objectMapper.writeValueAsString(registerRequest);

        // when
        ResultActions response = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        // then
        response.andExpect(status().isBadRequest());
        response.andExpect(jsonPath("$.length()").value(2));
        response.andExpect(jsonPath("$.errorMessage", is("password - Пароль должен содержать от 8 до 20 символов")));
        response.andExpect(jsonPath("$.errorCode", is(400)));
    }

    @Test
    void authenticate_whenValidAuthenticationRequest_thenStatusOk() throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "p@ssw0rd",
                USER
        );

        var result = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var userResponse = objectMapper.readValue(result.getContentAsString(), UserResponse.class);

        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "p@ssw0rd"
        );
        var jsonRequest = objectMapper.writeValueAsString(authRequest);

        // when
        ResultActions response = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        // then
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.length()").value(2));
        response.andExpect(jsonPath("$.access_token").isNotEmpty());
        response.andExpect(jsonPath("$.refresh_token").isNotEmpty());

        assertFalse(tokenRepository.findAllValidTokenByUser(userResponse.id()).isEmpty());
    }

    @Test
    void authenticate_whenUserNotRegistered_thenStatusNotFound() throws Exception {
        // given
        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "p@ssw0rd"
        );
        var jsonRequest = objectMapper.writeValueAsString(authRequest);

        // when
        ResultActions response = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        // then
        response.andExpect(status().isNotFound());
        response.andExpect(jsonPath("$.length()").value(2));
        response.andExpect(jsonPath("$.errorMessage", is("Пользователя с таким логином не существует!")));
        response.andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void authenticate_whenInvalidPasswordInAuthRequest_thenStatusBadRequest() throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "p@ssw0rd",
                USER
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "wrond_password"
        );
        var jsonRequest = objectMapper.writeValueAsString(authRequest);

        // when
        ResultActions response = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        // then
        response.andExpect(status().isBadRequest());
        response.andExpect(jsonPath("$.length()").value(2));
        response.andExpect(jsonPath("$.errorMessage", is("Неверный пароль, попробуйте заново.")));
        response.andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    void refreshToken_whenValidRequest_thenStatusOk() throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "p@ssw0rd",
                USER
        );
        var jsonRegisterRequest = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegisterRequest))
                .andExpect(status().isOk());

        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "p@ssw0rd"
        );
        var jsonAuthRequest = objectMapper.writeValueAsString(authRequest);

        var result = mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthRequest))
                .andExpect(status().isOk())
                .andReturn();

        var authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthenticationResponse.class);

        // when
        ResultActions refreshResponse = mockMvc.perform(post("/api/refresh-token")
                .header("Authorization", "Bearer " + authenticationResponse.refreshToken()));

        // then
        refreshResponse.andExpect(status().isOk());
    }

    @Test
    void refreshToken_whenNoTokenInAuthHeader_thenStatusBadRequest() throws Exception {
        // when
        ResultActions response = mockMvc.perform(post("/api/refresh-token"));

        // then
        response.andExpect(status().isBadRequest());
        response.andExpect(jsonPath("$.length()").value(2));
        response.andExpect(jsonPath("$.errorMessage",
                is("Значение заголовка 'Authorization' некорректно или отсутствует!")));
        response.andExpect(jsonPath("$.errorCode").value(404));
    }
}