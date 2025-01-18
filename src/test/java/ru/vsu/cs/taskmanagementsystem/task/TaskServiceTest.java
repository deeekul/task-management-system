package ru.vsu.cs.taskmanagementsystem.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;
import ru.vsu.cs.taskmanagementsystem.task.adapter.TaskMapper;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.TaskRepository;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.Task;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response.TaskResponse;
import ru.vsu.cs.taskmanagementsystem.task.exception.TaskNotFoundException;
import ru.vsu.cs.taskmanagementsystem.task.exception.UnauthorizedTaskAccessException;
import ru.vsu.cs.taskmanagementsystem.task.util.TaskAccessValidator;
import ru.vsu.cs.taskmanagementsystem.user.UserService;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TaskAccessValidator taskAccessValidator;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_shouldReturnPageOfTaskResponses_whenCalled() {
        //given
        var existingTasks = List.of(
                Task.builder()
                        .id(1L)
                        .title("Анализ производительности приложения")
                        .description("Провести анализ производительности и выявить узкие места")
                        .status(TaskStatus.CANCELED)
                        .priority(TaskPriority.HIGH)
                        .author(mock(User.class))
                        .assignee(mock(User.class))
                        .build(),
                Task.builder()
                        .id(2L)
                        .title("Рефакторинг модуля авторизации")
                        .description("Улучшить производительность и безопасность модуля авторизации")
                        .status(TaskStatus.IN_PROGRESS)
                        .priority(TaskPriority.MEDIUM)
                        .author(mock(User.class))
                        .assignee(mock(User.class))
                        .build()
        );

        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(existingTasks.get(0)));
        Pageable pageRequest = PageRequest.of(0, 10);

        var mappedTask = TaskResponse.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .status(TaskStatus.CANCELED)
                .priority(TaskPriority.HIGH)
                .author(new UserResponse(
                                1L,
                                "Александр",
                                "Малышев",
                                "malyshev_a",
                                Role.USER
                        )
                )
                .assignee(new UserResponse(
                        2L,
                        "Алексей",
                        "Минаков",
                        "minakov_a",
                        Role.USER
                ))
                .build();


        doReturn(taskPage)
                .when(taskRepository)
                .findAll(TaskStatus.CANCELED, TaskPriority.HIGH, pageRequest);

        doReturn(mappedTask)
                .when(taskMapper)
                .map(existingTasks.get(0));

        // when
        var result = taskService.getAllTasks(TaskStatus.CANCELED, TaskPriority.HIGH, pageRequest);

        // then
        var expectedResult = TaskResponse.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .status(TaskStatus.CANCELED)
                .priority(TaskPriority.HIGH)
                .author(new UserResponse(
                                1L,
                                "Александр",
                                "Малышев",
                                "malyshev_a",
                                Role.USER
                        )
                )
                .assignee(new UserResponse(
                        2L,
                        "Алексей",
                        "Минаков",
                        "minakov_a",
                        Role.USER
                        )
                )
                .build();

        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(expectedResult);

        verify(taskRepository, times(1)).findAll(TaskStatus.CANCELED, TaskPriority.HIGH, pageRequest);
        verify(taskMapper, times(1)).map(any(Task.class));
        verifyNoMoreInteractions(taskRepository);
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    void getTaskById_shouldReturnTaskResponse_whenTaskExistsAndUserHasAccess() {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Алексей")
                .lastName("Минаков")
                .login("minakov_a")
                .password("encoded_password")
                .role(Role.USER)
                .build();
        var userResponse = new UserResponse(
                1L,
                "Алексей",
                "Минаков",
                "minakov_a",
                Role.USER
        );

        var taskId = 1L;
        var foundTask = Task.builder()
                .id(taskId)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .author(mock(User.class))
                .assignee(user)
                .build();

        var taskResponse = TaskResponse.builder()
                .id(taskId)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .author(mock(UserResponse.class))
                .assignee(new UserResponse(
                        2L,
                        "Алексей",
                        "Минаков",
                        "minakov_a",
                        Role.USER
                        )
                )
                .build();

        doReturn(Optional.of(foundTask))
                .when(taskRepository)
                .findById(taskId);

        doReturn(userResponse)
                .when(userService)
                .getUserByLogin("minakov_a");

        doReturn(user)
                .when(userMapper)
                .map(userResponse);

        doNothing()
                .when(taskAccessValidator)
                .checkUserAccess(foundTask, user);

        doReturn(taskResponse)
                .when(taskMapper)
                .map(foundTask);

        var principal = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // when
        var result = taskService.getTaskById(taskId, principal);

        // then
        assertThat(result).isEqualTo(taskResponse);
        assertThat(result.title()).isEqualTo("Анализ производительности приложения");

        verify(taskRepository, times(1)).findById(taskId);
        verify(userService, times(1)).getUserByLogin("minakov_a");
        verify(userMapper, times(1)).map(userResponse);
        verify(taskAccessValidator, times(1)).checkUserAccess(foundTask, user);
        verify(taskMapper, times(1)).map(foundTask);

        verifyNoMoreInteractions(taskRepository, userService, taskMapper,taskAccessValidator, userService);
    }

    @Test
    void getTaskById_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Алексей")
                .lastName("Минаков")
                .login("minakov_a")
                .password("encoded_password")
                .role(Role.USER)
                .build();

        Long taskId = 1L;
        var optionalEmpty = Optional.empty();

        doReturn(optionalEmpty)
                .when(taskRepository)
                .findById(taskId);

        var principal = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // then
        assertThatThrownBy(() -> taskService.getTaskById(taskId, principal))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Задача с id = 1 не найдена!");

        // then
        verify(taskRepository, times(1)).findById(taskId);
        verifyNoInteractions(taskMapper, taskAccessValidator,userService);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getTaskById_shouldThrowUnauthorizedTaskAccessException_whenTaskExistsButUserDoesNotHaveAccess() {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Алексей")
                .lastName("Минаков")
                .login("minakov_a")
                .password("encoded_password")
                .role(Role.USER)
                .build();
        var userResponse = new UserResponse(
                1L,
                "Алексей",
                "Минаков",
                "minakov_a",
                Role.USER
        );

        var taskId = 1L;
        var foundTask = Task.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .author(User.builder()
                        .id(2L)
                        .build())
                .assignee(User.builder()
                        .id(3L)
                        .build())
                .build();

        doReturn(Optional.of(foundTask))
                .when(taskRepository)
                .findById(taskId);

        doReturn(userResponse)
                .when(userService)
                .getUserByLogin("minakov_a");

        doReturn(user)
                .when(userMapper)
                .map(userResponse);

        doThrow(new UnauthorizedTaskAccessException("Вы не имеете доступа к этой задаче!", HttpStatus.FORBIDDEN))
                .when(taskAccessValidator)
                .checkUserAccess(foundTask, user);

        var principal = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // then
        assertThatThrownBy(() -> taskService.getTaskById(taskId, principal))
                .isInstanceOf(UnauthorizedTaskAccessException.class)
                .hasMessageContaining("Вы не имеете доступа к этой задаче!");

        verify(taskRepository, times(1)).findById(taskId);
        verify(userService, times(1)).getUserByLogin("minakov_a");
        verify(userMapper, times(1)).map(userResponse);
        verify(taskAccessValidator, times(1)).checkUserAccess(foundTask, user);
        verify(taskMapper, never()).map(foundTask);
        verifyNoMoreInteractions(taskRepository, taskAccessValidator, userService);
    }

    @Test
    void getTaskByTitle_shouldReturnTaskResponse_whenTaskExists() {
        // given
        var title = "Анализ производительности приложения";
        var task = Task.builder()
                .id(1L)
                .title(title)
                .build();
        var taskResponse = TaskResponse.builder()
                .id(1L)
                .title(title)
                .build();

        doReturn(Optional.of(task))
                .when(taskRepository)
                .findByTitle(title);

        doReturn(taskResponse)
                .when(taskMapper)
                .map(task);

        // when
        var result = taskService.getTaskByTitle(title);

        // then
        var expectedResult = TaskResponse.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .build();

        assertThat(result).isEqualTo(expectedResult);
        verify(taskRepository, times(1)).findByTitle(title);
        verify(taskMapper, times(1)).map(task);
        verifyNoMoreInteractions(taskRepository);
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    void getTaskByTitle_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
       // given
        String title = "Nonexistent title";
        var optionalEmpty = Optional.empty();

        doReturn(optionalEmpty)
                .when(taskRepository)
                .findByTitle(title);

        // then
        assertThatThrownBy(() -> taskService.getTaskByTitle(title))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Задача с title = Nonexistent title не найдена!");

        verify(taskRepository, times(1)).findByTitle(title);
        verifyNoInteractions(taskMapper);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getAllTasksByAuthorId_shouldReturnPageOfTaskResponses_whenAuthorExistsAndUserHasAccess() {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Алексей")
                .lastName("Минаков")
                .login("minakov_a")
                .password("encoded_password")
                .role(Role.USER)
                .build();
        var userResponse = new UserResponse(
                1L,
                "Алексей",
                "Минаков",
                "minakov_a",
                Role.USER
        );

        var existingTasks = List.of(
                Task.builder()
                        .id(1L)
                        .title("Анализ производительности приложения")
                        .description("Провести анализ производительности и выявить узкие места")
                        .status(TaskStatus.CANCELED)
                        .priority(TaskPriority.HIGH)
                        .author(User.builder()
                                .id(1L)
                                .build())
                        .assignee(User.builder()
                                .id(2L)
                                .build())
                        .build(),
                Task.builder()
                        .id(2L)
                        .title("Рефакторинг модуля авторизации")
                        .description("Улучшить производительность и безопасность модуля авторизации")
                        .status(TaskStatus.IN_PROGRESS)
                        .priority(TaskPriority.MEDIUM)
                        .author(User.builder()
                                .id(3L)
                                .build())
                        .assignee(User.builder()
                                .id(2L)
                                .build())
                        .build()
        );

        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(existingTasks.get(0)));
        Pageable pageRequest = PageRequest.of(0, 10);

        var mappedTask = TaskResponse.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .status(TaskStatus.CANCELED)
                .priority(TaskPriority.HIGH)
                .author(new UserResponse(
                        1L,
                        "Алексей",
                        "Минаков",
                        "minakov_a",
                        Role.USER
                        )
                )
                .assignee(new UserResponse(
                        2L,
                        "Александр",
                        "Малышев",
                        "malyshev_a",
                        Role.USER
                ))
                .build();

        doReturn(userResponse)
                .when(userService)
                .getUserById(1L);

        doReturn(userResponse)
                .when(userService)
                .getUserByLogin("minakov_a");

        doReturn(user)
                .when(userMapper)
                .map(userResponse);

        doNothing()
                .when(taskAccessValidator)
                .checkUserAccess(1L, user);

        doReturn(taskPage)
                .when(taskRepository)
                .findAllByAuthorId(1L, null, null, pageRequest);

        doReturn(mappedTask)
                .when(taskMapper)
                .map(existingTasks.get(0));

        var principal = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // when
        var result = taskService.getAllTasksByAuthorId(1L, null, null, pageRequest, principal);

        // then
        var expectedResult = TaskResponse.builder()
                .id(1L)
                .title("Анализ производительности приложения")
                .description("Провести анализ производительности и выявить узкие места")
                .status(TaskStatus.CANCELED)
                .priority(TaskPriority.HIGH)
                .author(new UserResponse(
                                1L,
                                "Алексей",
                                "Минаков",
                                "minakov_a",
                                Role.USER
                        )
                )
                .assignee(new UserResponse(
                        2L,
                        "Александр",
                        "Малышев",
                        "malyshev_a",
                        Role.USER
                ))
                .build();

        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(expectedResult);

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserByLogin("minakov_a");
        verify(userMapper, times(1)).map(userResponse);
        verify(taskAccessValidator, times(1)).checkUserAccess(1L, user);
        verify(taskRepository, times(1)).findAllByAuthorId(1L, null, null, pageRequest);
        verify(taskMapper, times(1)).map(existingTasks.get(0));
    }

    @Test
    void getAllTasksByAuthorId_shouldThrowUnauthorizedTaskAccessException_whenUserDoesNotHaveAccess() {
        // given
        var user = User.builder()
                .id(1L)
                .firstName("Алексей")
                .lastName("Минаков")
                .login("minakov_a")
                .password("encoded_password")
                .role(Role.USER)
                .build();
        var userResponse = new UserResponse(
                1L,
                "Алексей",
                "Минаков",
                "minakov_a",
                Role.USER
        );

        var author = User.builder()
                .id(2L)
                .build();
        var authorResponse = UserResponse.builder()
                .id(2L)
                .build();
        var pageRequest = PageRequest.of(0, 10);

        doReturn(authorResponse)
                .when(userService)
                .getUserById(2L);

        doReturn(userResponse)
                .when(userService)
                .getUserByLogin("minakov_a");

        doReturn(user)
                .when(userMapper)
                .map(userResponse);

        doThrow(new UnauthorizedTaskAccessException("Вы не имеете доступа просматривать чужие задачи!"))
                .when(taskAccessValidator)
                .checkUserAccess(author.getId(), user);

        var principal = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // then
        assertThatThrownBy(() -> taskService.getAllTasksByAuthorId(author.getId(), null, null, pageRequest, principal))
                .isInstanceOf(UnauthorizedTaskAccessException.class)
                .hasMessageContaining("Вы не имеете доступа просматривать чужие задачи!");

        verify(userService, times(1)).getUserById(author.getId());
        verify(userService, times(1)).getUserByLogin("minakov_a");
        verify(userMapper, times(1)).map(userResponse);
        verify(taskAccessValidator, times(1)).checkUserAccess(author.getId(), user);

        verifyNoInteractions(taskMapper, taskRepository);
        verifyNoMoreInteractions(userService, userMapper, taskAccessValidator);
    }

    @Test
    void deleteTaskById_shouldDeleteTask_whenTaskExistsAndHaveAdminRole() {
        // given
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .build();

        doReturn(Optional.of(task))
                .when(taskRepository)
                        .findById(taskId);
        doNothing()
                .when(taskRepository)
                .delete(task);

        // when
        taskService.deleteTaskById(taskId);

        // then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(task);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void deleteTaskById_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        // given
        Long taskId = 100L;
        var optionalEmpty = Optional.empty();

        doReturn(optionalEmpty)
                .when(taskRepository)
                        .findById(taskId);


        // then
        assertThatThrownBy(() -> taskService.deleteTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Задача с id = 100 не найдена!");

        verify(taskRepository, times(1)).findById(taskId);
        verifyNoMoreInteractions(taskRepository);
    }
}