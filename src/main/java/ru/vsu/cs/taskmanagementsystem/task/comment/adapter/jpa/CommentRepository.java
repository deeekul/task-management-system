package ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}