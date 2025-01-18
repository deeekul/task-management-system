package ru.vsu.cs.taskmanagementsystem.task.adapter.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.Task;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;

import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
            SELECT t FROM Task t
            INNER JOIN FETCH t.author a
            LEFT JOIN FETCH t.assignee ass
            LEFT JOIN FETCH t.comments c
            WHERE (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            """)
    Page<Task> findAll(@Param("status") TaskStatus status,
                       @Param("priority") TaskPriority priority,
                       Pageable pageable);

    @Query(value = """
            SELECT t FROM Task t
            INNER JOIN FETCH t.author a
            LEFT JOIN FETCH t.assignee ass
            LEFT JOIN FETCH t.comments c
            WHERE a.id = :author_id
            AND (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            """)
    Page<Task> findAllByAuthorId(@Param("author_id") Long authorId,
                                 @Param("status") TaskStatus status,
                                 @Param("priority") TaskPriority priority,
                                 Pageable pageable);

    @Query(value = """
            SELECT t FROM Task t
            INNER JOIN FETCH t.author a
            LEFT JOIN FETCH t.assignee ass
            LEFT JOIN FETCH t.comments c
            WHERE ass.id = :assignee_id
            AND (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            """)
    Page<Task> findAllByAssigneeId(@Param("assignee_id") Long assigneeId,
                                   @Param("status") TaskStatus status,
                                   @Param("priority") TaskPriority priority,
                                   Pageable pageable);

    Optional<Task> findByTitle(String title);
}
