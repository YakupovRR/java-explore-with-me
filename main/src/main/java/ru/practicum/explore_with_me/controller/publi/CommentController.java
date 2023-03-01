package ru.practicum.explore_with_me.controller.publi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.comment.CommentDto;
import ru.practicum.explore_with_me.service.comment.CommentService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getAllCommentsForEvent(@Positive @PathVariable Long eventId,
                                                   @RequestParam (defaultValue = "0") int from,
                                                   @RequestParam (defaultValue = "10") int size) {
        log.info("Получен GET запрос к эндпоинту /events/" + eventId + "/comments");
        return commentService.getAllCommentsForEvent(eventId, from, size);
    }
}
