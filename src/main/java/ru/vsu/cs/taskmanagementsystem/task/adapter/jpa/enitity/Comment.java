package ru.vsu.cs.taskmanagementsystem.task.adapter.jpa.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
@Entity
public class Comment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comments_sequence")
    @SequenceGenerator(
            name = "comments_sequence",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String text;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
}
