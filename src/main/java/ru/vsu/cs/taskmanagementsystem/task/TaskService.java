package ru.vsu.cs.taskmanagementsystem.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;
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
import java.time.LocalDateTime;

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

    public TaskResponse getTaskById(Long taskId, Principal connectedUser) {
        var task = findTaskByIdOrThrowException(taskId);
        var user = getUserFromPrincipal(connectedUser);
        checkTaskAccess(task, user);
        return taskMapper.map(task);
    }

    public Page<TaskResponse> getAllTasksByAuthorId(Long authorId, TaskStatus status,
                                                    TaskPriority priority, Pageable pageable) {
        userService.getUserById(authorId);
        return taskRepository.findAllByAuthorId(authorId, status, priority, pageable)
                .map(taskMapper::map);
    }

    public Page<TaskResponse> getAllTasksByAssigneeId(Long assigneeId, TaskStatus status,
                                                      TaskPriority priority, Pageable pageable) {
        userService.getUserById(assigneeId);
        return taskRepository.findAllByAssigneeId(assigneeId, status, priority, pageable)
                .map(taskMapper::map);
    }

    public TaskResponse getTaskByTitle(String title) {
        var task = findTaskByTitleOrThrowException(title);
        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest taskRequest, Principal connectedUser) {
        var author = userService.getUserById(taskRequest.authorId());
        var assignee = userService.getUserById(taskRequest.assigneeId());

        var task = taskMapper.map(taskRequest);
        task.setAuthor(userMapper.map(author));
        task.setAssignee(userMapper.map(assignee));
        var savedTask = taskRepository.save(task);

        var comments = taskRequest.comments();
        if (!comments.isEmpty()) {
            for (var commentRequest : comments) {
                addCommentToTask(savedTask.getId(), commentRequest, connectedUser);
            }
        }
        return taskMapper.map(savedTask);
    }

    public TaskResponse addCommentToTask(Long id, CommentRequest commentRequest, Principal connectedUser) {
        var task = findTaskByIdOrThrowException(id);
        var user = getUserFromPrincipal(connectedUser);
        checkTaskAccess(task, user);

        var comment = Comment.builder()
                .text(commentRequest.text())
                .createdDate(LocalDateTime.now())
                .task(task)
                .user(user)
                .build();
        commentRepository.save(comment);

        task.addComment(comment);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse updateTaskById(Long id, TaskUpdateRequest taskUpdateRequest, Principal connectedUser) {
        var task = findTaskByIdOrThrowException(id);
        var user = getUserFromPrincipal(connectedUser);

        checkTaskAccess(task, user);
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

    private User getUserFromPrincipal(Principal connectedUser) {
        var login = (String) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userMapper.map(userService.getUserByLogin(login));
    }

    private void checkTaskAccess(Task task, User connectedUser) {
        if (!connectedUser.getRole().equals(Role.ADMIN) && !task.getAssignee().getId().equals(connectedUser.getId())) {
            throw new UnauthorizedTaskAccessException(
                    "Вы не имеете доступа к этой задаче!", HttpStatus.FORBIDDEN);
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