package ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskPriority;
import ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity.temp.TaskStatus;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa.entity.Comment;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
@Entity
public class Task {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tasks_sequence")
    @SequenceGenerator(
            name = "tasks_sequence",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}