package ru.practicum.explore_with_me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.model.Comment;
import ru.practicum.explore_with_me.model.CommentState;
import ru.practicum.explore_with_me.model.User;
import ru.practicum.explore_with_me.model.event.Event;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByEventAndState(Event event, CommentState state, Pageable pageable);

    Page<Comment> findAllByUser(User user, Pageable pageable);

    Optional<Comment> findByIdAndUserId(Long id, Long userId);
}
