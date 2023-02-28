package ru.practicum.explore_with_me.controller.privat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.event.EventDto;
import ru.practicum.explore_with_me.model.dto.event.EventNewDto;
import ru.practicum.explore_with_me.model.dto.event.EventUpdDto;
import ru.practicum.explore_with_me.model.dto.event.EventUserDto;
import ru.practicum.explore_with_me.model.event.Event;
import ru.practicum.explore_with_me.model.StateAction;
import ru.practicum.explore_with_me.model.mapper.EventMapper;
import ru.practicum.explore_with_me.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventDto> addEvent(@PathVariable long userId, @RequestBody @Valid EventNewDto dto) {
        log.info("Получен Post запрос к эндпоинту users" + userId + "/events");
        EventDto newDto = eventMapper.toDto(eventService.addEvent(userId, eventMapper.toEventFromNewDto(dto)));
        return new ResponseEntity<>(newDto, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getEventByUser(@PathVariable long userId,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен Get запрос к эндпоинту users" + userId + "/events");

        List<EventDto> dtos = eventService.getEventByUser(userId, from, size);
        return dtos;
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable long userId, @PathVariable long eventId,
                           @RequestBody @Valid EventUpdDto eventDto) {
        log.info("Получен Patch запрос к эндпоинту users" + userId + "/events/" + eventId);
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            EventDto dto = eventMapper.toDto(eventService.cancelEvent(userId, eventId));
            return dto;
        } else if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            Event event = eventMapper.toEventFromUpdDto(eventDto);
            return eventMapper.toDto(eventService.updateEvent(userId, eventId, event, eventDto.getStateAction()));
        } else {
            Event event = eventMapper.toEventFromUpdDto(eventDto);
            return eventMapper.toDto(eventService.updateEvent(userId, eventId, event, null));
        }
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventUserDto getUserEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Получен Get запрос к эндпоинту users" + userId + "/events/" + eventId);
        return eventService.getUserEvent(userId, eventId);
    }

}
