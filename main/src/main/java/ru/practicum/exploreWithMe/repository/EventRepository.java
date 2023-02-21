package ru.practicum.exploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.exploreWithMe.model.event.Event;
import ru.practicum.exploreWithMe.model.event.EventState;
import ru.practicum.exploreWithMe.model.User;

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
