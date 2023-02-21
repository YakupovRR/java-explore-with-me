package ru.practicum.explore_with_me.controller.publi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.event.EventDto;
import ru.practicum.explore_with_me.model.dto.event.EventShortDto;
import ru.practicum.explore_with_me.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Integer[] categories,
                                               @RequestParam(required = false) Boolean paid,

                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,

                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,

                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest httpServletRequest) {

        List<EventShortDto> shortDtos = eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, httpServletRequest);
        log.info("Получен Get запрос к эндпоинту events");
        return shortDtos;
    }

    @GetMapping("/events/{id}")
    public EventDto getEventById(@PathVariable long id, HttpServletRequest httpServletRequest) {
        log.info("Получен Get запрос к эндпоинту events/" + id);
        return eventService.getEventById(id, httpServletRequest);
    }

}
