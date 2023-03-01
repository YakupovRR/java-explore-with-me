package ru.practicum.explore_with_me.model.mapper;


import ru.practicum.explore_with_me.model.dto.comment.CommentDto;
import ru.practicum.explore_with_me.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.explore_with_me.model.CommentState.*;


public class CommentMapper {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .createdOn(comment.getCreatedOn().format(DATE_TIME_FORMATTER))
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment
                .builder()
                .text(commentDto.getText())
                .createdOn(LocalDateTime.now())
                .state(NEW)
                .build();
    }
}
