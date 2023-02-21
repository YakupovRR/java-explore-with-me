package ru.practicum.explore_with_me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explore_with_me.model.event.Event;
import ru.practicum.explore_with_me.model.event.EventState;
import ru.practicum.explore_with_me.model.User;

import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor {

    Page<Event> findAllByInitiatorId(Long id, Pageable pageable);

    Event findByIdAndInitiator(Long id, User initiator);

    Event findByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("SELECT e FROM Event e WHERE e.id in :ids")
    Set<Event> getEventsById(Set<Long> ids);

    Event findByIdAndState(Long id, EventState state);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM events WHERE category_id=:catId")
    int getCountByCategory(long catId);
}
