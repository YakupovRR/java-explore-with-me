package ru.practicum.explore_with_me.controller.privat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.comment.CommentDto;
import ru.practicum.explore_with_me.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    public CommentPrivateController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId) {
        log.info("Получен POST запрос к эндпоинту /users/" + userId + "/comments" + userId);
        return commentService.createComment(commentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@Positive @PathVariable Long commentId,
                                    @Positive @PathVariable Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен Patch запрос к эндпоинту /users/" + userId + "/comments" + commentId);
        return commentService.updateComment(commentId, userId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@Positive @PathVariable Long userId,
                              @Positive @PathVariable Long commentId) {
        log.info("Получен Delete запрос к эндпоинту /users/" + userId + "/comments" + commentId);
        commentService.deleteComment(commentId, userId);
    }

    @GetMapping
    public List<CommentDto> getAllCommentsByUser(@Positive @PathVariable Long userId,
                                                 @RequestParam (defaultValue = "0") int from,
                                                 @RequestParam (defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту /users/" + userId + "/comments");
        return commentService.getAllCommentsByUser(userId, from, size);
    }
}
