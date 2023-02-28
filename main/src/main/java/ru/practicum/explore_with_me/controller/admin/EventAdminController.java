package ru.practicum.explore_with_me.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.StateAction;
import ru.practicum.explore_with_me.model.dto.event.EventAdminDto;
import ru.practicum.explore_with_me.model.dto.event.EventDto;
import ru.practicum.explore_with_me.model.dto.event.EventFullDto;
import ru.practicum.explore_with_me.model.mapper.EventMapper;
import ru.practicum.explore_with_me.service.event.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PatchMapping("/admin/events/{eventId}")
    public EventDto updateEvent(@PathVariable long eventId, @RequestBody EventAdminDto eventAdminDto) {
        log.info("Получен Patch запрос к эндпоинту admin/events/", eventAdminDto);
        if (eventAdminDto.getStateAction() != null) {
            if (eventAdminDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                return eventMapper.toDto(eventService.publishEvent(eventId, eventMapper.toEventFromAdminDto(eventAdminDto)));
            }
            if (eventAdminDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                return eventMapper.toDto(eventService.rejectEvent(eventId, eventMapper.toEventFromAdminDto(eventAdminDto)));
            }
        } else {
            return eventMapper.toDto(eventService.updateAdmin(eventId, eventMapper.toEventFromAdminDto(eventAdminDto)));
        }
        return null;
    }

    @GetMapping("/admin/events")
    public List<EventDto> getEventsAdmin(@RequestParam(required = false) Long[] usersId,
                                         @RequestParam(required = false) String[] states,
                                         @RequestParam(required = false) Long[] catId,

                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime rangeStart,

                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,

                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {

        List<EventDto> listOfDto = eventService.getEventsAdmin(usersId, states, catId, rangeStart, rangeEnd, from, size);
        log.info("Получен Get запрос к эндпоинту admin/events",
                usersId, states, catId, rangeStart, rangeEnd, from, size);

        return listOfDto;
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto updateAdmin(@PathVariable long eventId, @RequestBody EventAdminDto updDto) {
        log.info("Получен Put запрос к эндпоинту admin/events", eventId);
        return eventMapper.toFullDto(eventService.updateAdmin(eventId, eventMapper.toEventFromAdminDto(updDto)));
    }

}
