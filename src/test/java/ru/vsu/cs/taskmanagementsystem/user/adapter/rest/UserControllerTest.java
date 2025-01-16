package ru.vsu.cs.taskmanagementsystem.user.adapter.rest;

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
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.ADMIN;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.USER;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_whenGetUsers_thenStatusOk() throws Exception {
        // given
        var users = List.of(
                User.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .login("ivanov_i")
                        .password("encoded_password1")
                        .role(ADMIN)
                        .build(),
                User.builder()
                        .id(2L)
                        .firstName("Андрей")
                        .lastName("Чураков")
                        .login("churakov_a")
                        .password("encoded_password2")
                        .role(USER)
                        .build()
        );
        userRepository.saveAll(users);

        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getAllUsers_whenInvalidTokenOrAbsent_thenStatusForbidden() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    void getUserById_whenUserExists_thenStatusOk() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password")
                .role(ADMIN)
                .build();
        var savedUser = userRepository.save(user);

        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{id}", savedUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.login", is("ivanov_i")));
    }

    @Test
    void getUserById_whenUserDoesNotExist_thenStatusNotFound() throws Exception {
        // given
        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{id}", 50L)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.errorMessage", is("Пользователь с id = 50 не найден!")));
        result.andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void getUsersById_whenInvalidTokenOrAbsent_thenStatusForbidden() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password")
                .role(ADMIN)
                .build();
        userRepository.save(user);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{}", 1L)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    void getUserByLogin_whenUserExists_thenStatusOk() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password1")
                .role(ADMIN)
                .build();
        userRepository.save(user);

        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(get("/api/users/login?login={value}", "ivanov_i")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.length()").value(5));
        result.andExpect(jsonPath("$.login", is("ivanov_i")));
    }

    @Test
    void getUserByLogin_whenUserDoesNotExist_thenStatusNotFound() throws Exception {
        // given
        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(get("/api/users/login?login={value}", "ivanov_i")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.errorMessage", is("Пользователь с login = ivanov_i не найден!")));
        result.andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void getUsersByLogin_whenInvalidTokenOrAbsent_thenStatusForbidden() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password")
                .role(USER)
                .build();
        userRepository.save(user);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/login?login={value}", "ivanov_i")
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    void deleteUserById_whenAdminDeleteExistingUser_thenStatusOk() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password")
                .role(USER)
                .build();
        var savedUser = userRepository.save(user);

        registerUser(ADMIN);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", savedUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void deleteUserById_whenDeleteExistingUserWithoutAccessRights_thenStatusForbidden() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("encoded_password1")
                .role(USER)
                .build();
        userRepository.save(user);

        registerUser(USER);
        var accessToken = authenticate(
                AuthenticationRequest.builder()
                        .login("cheryshev_a")
                        .password("p@ssw0rd")
                        .build()
        ).accessToken();

        // when
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        // then
        result.andExpect(status().isForbidden());
    }

    /**
     * Регистрирует пользователя с указанной ролью.
     * Используется в тестах с целью выполнения запросов к API.
     *
     * @param role Роль пользователя (USER, ADMIN)
     */
    private void registerUser(Role role) throws Exception {
        // given
        var registerRequest = new RegisterRequest(
                "Черышев",
                "Андрей",
                "cheryshev_a",
                "p@ssw0rd",
                role
        );
        var jsonRequest = objectMapper.writeValueAsString(registerRequest);
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    /**
     * Аутентифицирует пользователя с предоставленными учетными данными.
     * Используется в тестах для выполнения авторизованных запросов к API.
     *
     * @param authRequest Объект AuthenticationRequest, содержащий логин и пароль пользователя.
     * @return Объект AuthenticationResponse, содержащий accessToken и refreshToken.
     */
    private AuthenticationResponse authenticate(AuthenticationRequest authRequest) throws Exception {
        var jsonRequest = objectMapper.writeValueAsString(authRequest);

        var result = mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        var authResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                AuthenticationResponse.class);
        return authResponse;
    }
}