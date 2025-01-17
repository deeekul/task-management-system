package ru.vsu.cs.taskmanagementsystem.task.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.Task;
import ru.vsu.cs.taskmanagementsystem.task.exception.UnauthorizedTaskAccessException;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

@Component
public class TaskAccessValidator {

    public void checkUserAccess(Task task, User connectedUser) {
        if (!connectedUser.getRole().equals(Role.ADMIN) && !task.getAssignee().getId().equals(connectedUser.getId())) {
            throw new UnauthorizedTaskAccessException(
                    "Вы не имеете доступа к этой задаче!", HttpStatus.FORBIDDEN);
        }
    }

    public void checkUserAccess(Long id, User user) {
        if (!user.getRole().equals(Role.ADMIN) && !user.getId().equals(id)) {
            throw new UnauthorizedTaskAccessException(
                    "У вас нет возможности просматривать чужие задачи!", HttpStatus.FORBIDDEN);
        }
    }
}
