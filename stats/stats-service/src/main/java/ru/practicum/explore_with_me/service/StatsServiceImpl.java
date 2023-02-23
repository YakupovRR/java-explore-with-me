package ru.practicum.explore_with_me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.model.Hit;
import ru.practicum.explore_with_me.model.ViewStats;
import ru.practicum.explore_with_me.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Hit addHit(Hit hit) {
        return repository.save(hit);
    }

    @Override
    public List<ViewStats> getViewStats(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime startDto;
        LocalDateTime endDto;
        if (start != null && end != null) {
            startDto = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), dateTimeFormatter);
            endDto = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), dateTimeFormatter);
        } else {
            startDto = LocalDateTime.parse(URLDecoder.decode("1971-01-01 00:00:00", StandardCharsets.UTF_8), dateTimeFormatter);
            endDto = LocalDateTime.parse(URLDecoder.decode("2101-01-01 00:00:00", StandardCharsets.UTF_8), dateTimeFormatter);
        }
        List<ViewStats> result;
        if (unique) {
            result = repository.getAllWithUniqueIp(startDto, endDto, uris);
        } else {
            result = repository.getAll(startDto, endDto, uris);
        }
        return result;
    }
}
