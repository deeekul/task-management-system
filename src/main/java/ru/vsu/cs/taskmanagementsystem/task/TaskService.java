package ru.vsu.cs.taskmanagementsystem.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.taskmanagementsystem.task.adapter.TaskMapper;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.TaskRepository;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.Task;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskCreateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.request.TaskUpdateRequest;
import ru.vsu.cs.taskmanagementsystem.task.adapter.rest.dto.response.TaskResponse;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa.CommentRepository;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa.entity.Comment;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;
import ru.vsu.cs.taskmanagementsystem.task.exception.TaskNotFoundException;
import ru.vsu.cs.taskmanagementsystem.task.exception.UnauthorizedTaskAccessException;
import ru.vsu.cs.taskmanagementsystem.user.UserService;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

import java.security.Principal;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final TaskMapper taskMapper;

    private final UserMapper userMapper;

    private final UserService userService;

    public Page<TaskResponse> getAllTasks(TaskStatus status, TaskPriority priority, Pageable pageable) {
        return taskRepository.findAll(status, priority, pageable)
                .map(taskMapper::map);
    }

    public TaskResponse getTaskById(Long taskId) {
        var task = findTaskByIdOrThrowException(taskId);
        return taskMapper.map(task);
    }

    public Page<TaskResponse> getAllTasksByAuthorId(Long id, TaskStatus status,
                                                    TaskPriority priority, Pageable pageable) {
        userService.getUserById(id);
        return taskRepository.findAllByAuthorId(id, status, priority, pageable)
                .map(taskMapper::map);
    }

    public Page<TaskResponse> getAllTasksByAssigneeId(Long id, TaskStatus status,
                                                      TaskPriority priority,Pageable pageable) {
        userService.getUserById(id);
        return taskRepository.findAllByAssigneeId(id, status, priority, pageable)
                .map(taskMapper::map);
    }

    public TaskResponse getTaskByTitle(String title) {
        var task = findTaskByTitleOrThrowException(title);
        return taskMapper.map(task);
    }

    public TaskResponse createTask(TaskCreateRequest taskRequest) {
        var task = taskMapper.map(taskRequest);
        var savedTask = taskRepository.save(task);

        return taskMapper.map(savedTask);
    }

    @Transactional
    public TaskResponse addCommentToTask(Long id, CommentRequest commentRequest, Principal connectedUser) {
        var task = findTaskByIdOrThrowException(id);
        var login = (String) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        var user = userMapper.map(userService.getUserByLogin(login));

        checkUserAccess(task, user);
        var comment = Comment.builder()
                .text(commentRequest.text())
                .task(task)
                .user(user)
                .build();

        commentRepository.save(comment);
        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse updateTaskById(Long id, TaskUpdateRequest taskUpdateRequest, Principal connectedUser) {
        var task = findTaskByIdOrThrowException(id);
        var login = (String) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        var user = userMapper.map(userService.getUserByLogin(login));

        checkUserAccess(task, user);
        updateTaskFields(task, taskUpdateRequest);

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void deleteTaskById(Long id) {
        var task = findTaskByIdOrThrowException(id);
        taskRepository.delete(task);
    }

    private void updateTaskFields(Task task, TaskUpdateRequest request) {
        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.assigneeId() != null) {
            var newAssignee = userMapper.map(userService.getUserById(request.assigneeId()));
            task.setAssignee(newAssignee);
        }
    }

    private void checkUserAccess(Task task, User user) {
        if (!user.getRole().name().equals("ADMIN") ||
                !task.getAssignee().getId().equals(user.getId())) {
            throw new UnauthorizedTaskAccessException("Вы не имеете право изменять эту задачу!", HttpStatus.FORBIDDEN);
        }
    }

    private Task findTaskByIdOrThrowException(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(
                        () -> new TaskNotFoundException("Задача с id = " + taskId + " не найдена!",
                                HttpStatus.NOT_FOUND)
                );
    }

    private Task findTaskByTitleOrThrowException(String title) {
        return taskRepository.findByTitle(title)
                .orElseThrow(
                        () -> new TaskNotFoundException("Задача с title = " + title + " не найдена!",
                                HttpStatus.NOT_FOUND)
                );
    }
}