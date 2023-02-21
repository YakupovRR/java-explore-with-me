package ru.practicum.exploreWithMe.service.event;

import ru.practicum.exploreWithMe.model.event.Event;
import ru.practicum.exploreWithMe.model.StateAction;
import ru.practicum.exploreWithMe.model.dto.event.EventDto;
import ru.practicum.exploreWithMe.model.dto.event.EventShortDto;
import ru.practicum.exploreWithMe.model.dto.event.EventUserDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(long userId, Event event);

    Event publishEvent(long id, Event eventIn);

    Event rejectEvent(long id, Event eventIn);

    List<EventDto> getEventsAdmin(Long[] usersId, String[] states, Long[] catId,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsPublic(String text, Integer[] category, Boolean paid, LocalDateTime dt1,
                                        LocalDateTime dt2, Boolean onlyAvailable, String sort,
                                        Integer from, Integer size, HttpServletRequest httpServletRequest);

    EventDto getEventById(long id, HttpServletRequest httpServletRequest);

    List<EventDto> getEventByUser(long id, Integer from, Integer size);

    Event updateEvent(long userId, long eventId, Event event, StateAction stateAction);

    Event updateAdmin(long id, Event event);

    EventUserDto getUserEvent(long userId, long eventId);

    Event cancelEvent(long userId, long eventId);
}
