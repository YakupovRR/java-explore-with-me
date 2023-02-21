package ru.practicum.exploreWithMe.controller;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.model.HitDto;
import ru.practicum.exploreWithMe.mapper.Mapper;
import ru.practicum.exploreWithMe.model.ViewStats;
import ru.practicum.exploreWithMe.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;
    private final Mapper mapper;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> hit(@RequestBody HitDto hitDto)
                    throws ValidationException {
            log.info("Получен запрос к эндпоинту hit");
        return new ResponseEntity<>(
                mapper.toHitDto(statsService.addHit(mapper.toHit(hitDto))), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStats> getViewStats(
            String start,
            String end,
            @RequestParam String[] uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получен запрос к эндпоинту stats", start, end, uris, unique);
        return statsService.getViewStats(start, end, uris, unique);
    }

}
