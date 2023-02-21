package ru.practicum.explore_with_me.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.model.Request;
import ru.practicum.explore_with_me.model.Status;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.id in :ids AND r.status=:status")
    List<Request> findAllByIdsAndStatus(List<Long> ids, Status status);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :requesterId AND r.id = :id")
    Request findByRequesterIdAndId(Long requesterId, Long id);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :requesterId AND r.event.id = :eventId")
    Request findByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :requester")
    List<Request> findAllByRequesterOrderByCreated(Long requester, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.id in :ids")
    List<Request> findAllByIds(List<Long> ids);

    List<Request> findAllByEventId(Long eventId, Pageable pageable);

    List<Request> findAllByEventIdAndStatus(Long eventId, Status status);

    @Query("SELECT COUNT(r.id) FROM Request r WHERE r.event.id=:eventId AND r.status='CONFIRMED'")
    Integer getCountConfirmed(Long eventId);
}
