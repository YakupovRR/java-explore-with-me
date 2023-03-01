package ru.practicum.explore_with_me.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.model.dto.comment.CommentDto;
import ru.practicum.explore_with_me.service.comment.CommentService;

import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    public CommentAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/{commentId}/approve")
    public CommentDto approveComment(@Positive @PathVariable Long commentId) {
        log.info("Получен Patch запрос к эндпоинту /admin/comments/", +  commentId + "/approve");
        return commentService.approveComment(commentId);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto rejectComment(@Positive @PathVariable Long commentId) {
        log.info("Получен Patch запрос к эндпоинту /admin/comments/", +  commentId + "/reject");
        return commentService.rejectComment(commentId);
    }
}
