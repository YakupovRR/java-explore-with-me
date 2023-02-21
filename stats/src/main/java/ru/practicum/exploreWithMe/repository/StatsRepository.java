package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.exploreWithMe.model.Hit;
import ru.practicum.exploreWithMe.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT new ru.practicum.exploreWithMe.model.ViewStats(COUNT(e.ip), e.app, e.uri) " +
            "FROM Hit e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app,e.uri ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.exploreWithMe.model.ViewStats(COUNT (distinct e.ip), e.app, e.uri) " +
            "FROM Hit e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app,e.uri ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getAllWithUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);

}
