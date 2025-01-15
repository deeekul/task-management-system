package ru.vsu.cs.taskmanagementsystem.task.comment.adapter;


import org.mapstruct.Mapper;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.jpa.entity.Comment;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.request.CommentRequest;
import ru.vsu.cs.taskmanagementsystem.task.comment.adapter.rest.dto.response.CommentResponse;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment map(CommentRequest commentRequest);

    CommentResponse map(Comment comment);
}
