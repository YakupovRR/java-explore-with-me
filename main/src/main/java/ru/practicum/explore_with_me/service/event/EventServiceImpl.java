package ru.practicum.explore_with_me.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.StatsClient;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.model.*;
import ru.practicum.explore_with_me.model.dto.event.EventDto;
import ru.practicum.explore_with_me.model.dto.event.EventFullDto;
import ru.practicum.explore_with_me.model.dto.event.EventShortDto;
import ru.practicum.explore_with_me.model.dto.event.EventUserDto;
import ru.practicum.explore_with_me.model.event.Event;
import ru.practicum.explore_with_me.model.event.EventState;
import ru.practicum.explore_with_me.model.event.EventsCountConfirmed;
import ru.practicum.explore_with_me.model.entity.QEvent;
import ru.practicum.explore_with_me.model.mapper.EventMapper;
import ru.practicum.explore_with_me.repository.CategoryRepository;
import ru.practicum.explore_with_me.repository.EventRepository;
import ru.practicum.explore_with_me.repository.RequestRepository;
import ru.practicum.explore_with_me.repository.UserRepository;
import ru.practicum.WebClient;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    private final RequestRepository requestRepository;

    private final WebClient webClient;

    private final StatsClient statsClient;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Boolean checkDiffTime(LocalDateTime dt1, LocalDateTime dt2, int diff) {
        return (dt2.minusHours(diff).isAfter(dt1) || dt2.minusHours(diff).isEqual(dt1));
    }

    @Override
    @Transactional
    public Event addEvent(long userId, Event event) {
        if (!checkDiffTime(LocalDateTime.now(), event.getEventDate(), 2)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND)));
        event.setCategory(categoryRepository.findById(event.getCategory().getId())
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND)));
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now().withNano(0));

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event publishEvent(long id, Event eventIn) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        if (event.getState().equals(EventState.PENDING) &&
                checkDiffTime(LocalDateTime.now().withNano(0), event.getEventDate(), 1)) {
            if (eventIn.getAnnotation() != null && !eventIn.getAnnotation().isBlank()) {
                event.setAnnotation(eventIn.getAnnotation());
            }
            if (eventIn.getTitle() != null && !eventIn.getTitle().isBlank()) {
                event.setTitle(eventIn.getTitle());
            }
            if (eventIn.getDescription() != null && !eventIn.getDescription().isBlank()) {
                event.setDescription(eventIn.getDescription());
            }
            if (eventIn.getParticipantLimit() != null) {
                event.setParticipantLimit(eventIn.getParticipantLimit());
            }
            if (eventIn.getEventDate() != null) {
                event.setEventDate(eventIn.getEventDate());
            }
            if (eventIn.getPaid() != null) {
                event.setPaid(eventIn.getPaid());
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now().withNano(0));
            return event;
        } else {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public Event rejectEvent(long id, Event eventIn) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
            return event;
        } else if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        } else {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<EventDto> getEventsAdmin(Long[] usersId, String[] states, Long[] catId,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events;
        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();

        if (!(usersId == null || usersId[0] == 0)) {
            predicate = predicate.and(qEvent.initiator.id.in(usersId));
        }

        if (states != null) {
            EventState[] sts = new EventState[3];
            for (int i = 0; i < Objects.requireNonNull(states).length; i++) {
                sts[i] = EventState.valueOf(states[i]);
            }
            predicate = predicate.and(qEvent.state.in(sts));
        }

        if (!(catId == null || catId[0] == 0)) {
            predicate = predicate.and(qEvent.category.id.in(catId));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicate = predicate.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        }
        events = eventRepository.findAll(predicate, pageable).toList();
        List<EventsCountConfirmed> countsConfirm = getConfirmed(events);
        List<EventDto> dtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
        List<Long> dtosId = new ArrayList<>();
        for (EventDto i : dtos) {
            dtosId.add(i.getId());
        }
        ViewStatsDto[] viewStatsDtos = webClient.getViews(dtosId);
        for (ViewStatsDto view : viewStatsDtos) {

            Long viewId = Long.parseLong(view.getUri().substring(view.getUri().lastIndexOf("/") + 1));
            dtos.stream()
                    .filter(e -> Objects.equals(e.getId(), viewId))
                    .forEach(e -> e.setViews(Math.toIntExact(view.getHits())));
        }



        for (EventsCountConfirmed eventsCount : countsConfirm) {
            dtos.stream().filter(eventDto -> eventDto.getId().equals(eventsCount.getId()))
                    .forEach(eventDto -> eventDto.setConfirmedRequests(eventsCount.getCount()));
        }
        dtos.sort(Comparator.comparing(EventDto::getId).reversed());
        return dtos;
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, Integer[] category, Boolean paid, LocalDateTime dt1,
                                               LocalDateTime dt2, Boolean onlyAvailable, String sort,
                                               Integer from, Integer size, HttpServletRequest httpServletRequest) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events;

        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();
        if (!(text == null || text.isBlank() || text.equals("0"))) {
            predicate = predicate.and(qEvent.annotation.toUpperCase().contains(text.toUpperCase())
                    .or(qEvent.description.toUpperCase().contains(text.toUpperCase())));
        }

        if (!(category == null || category[0] == 0)) {
            predicate = predicate.and(qEvent.category.id.in(category));
        }

        if (paid != null) {
            predicate = predicate.and(qEvent.paid.eq(paid));
        }

        if (dt1 != null && dt2 != null) {
            predicate = predicate.and(qEvent.eventDate.between(dt1, dt2));
        }

        events = eventRepository.findAll(predicate, pageable).getContent();

        List<EventsCountConfirmed> countsConfirm = getConfirmed(events);

        List<EventFullDto> fullDtos = events.stream().map(eventMapper::toFullDto).collect(Collectors.toList());
        statsClient.addToStatistic(httpServletRequest);
        List<Long> dtosId = new ArrayList<>();
        for (EventFullDto i : fullDtos) {
            dtosId.add(i.getId());
        }
        ViewStatsDto[] viewStatsDtos = webClient.getViews(dtosId);
        for (ViewStatsDto view : viewStatsDtos) {
            Long viewId = Long.parseLong(view.getUri().substring(view.getUri().lastIndexOf("/") + 1));
            fullDtos.stream()
                    .filter(eventShortDto -> Objects.equals(eventShortDto.getId(), viewId))
                    .forEach(eventShortDto -> eventShortDto.setViews(Math.toIntExact(view.getHits())));
        }

        for (EventsCountConfirmed eventsCount : countsConfirm) {
            fullDtos.stream().filter(eventFullDto -> eventFullDto.getId().equals(eventsCount.getId()))
                    .forEach(eventFullDto -> eventFullDto.setConfirmedRequests(eventsCount.getCount()));
        }
        List<EventShortDto> dtos = new ArrayList<>();
        if (onlyAvailable != null) {
            if (onlyAvailable) {
                dtos = fullDtos.stream()
                        .filter(e -> e.getConfirmedRequests() <= e.getParticipantLimit())
                        .map(eventMapper::toShortFromFull)
                        .collect(Collectors.toList());
            } else {
                dtos = fullDtos.stream()
                        .map(eventMapper::toShortFromFull)
                        .collect(Collectors.toList());
            }
        }

        if (sort != null) {
            if (sort.equals("VIEWS")) {
                dtos.sort(Comparator.comparing(EventShortDto::getViews));
            }
            if (sort.equals("EVENT_DATE")) {
                dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            }
        }

        return dtos;
    }

    @Override
    public EventDto getEventById(long id, HttpServletRequest httpServletRequest) {
        EventDto dto = eventMapper.toDto(eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND)));

        dto.setViews(webClient.findView(id));
        webClient.addToStatistic(httpServletRequest);
        dto.setConfirmedRequests(requestRepository.getCountConfirmed(id));
        return dto;
    }

    @Override
    public List<EventDto> getEventByUser(long id, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(id, pageable).getContent();
        List<EventDto> dtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
        List<Long> dtosId = new ArrayList<>();
        for (EventDto i : dtos) {
            dtosId.add(i.getId());
        }
        ViewStatsDto[] viewStatsDtos = webClient.getViews(dtosId);
        for (ViewStatsDto view : viewStatsDtos) {
            Long viewId = Long.parseLong(view.getUri().substring(view.getUri().lastIndexOf("/") + 1));
            dtos.stream()
                    .filter(eventDto -> Objects.equals(eventDto.getId(), viewId))
                    .forEach(eventDto -> eventDto.setViews(Math.toIntExact(view.getHits())));
        }

        List<EventsCountConfirmed> countsConfirm = getConfirmed(events);
        for (EventsCountConfirmed eventsCount : countsConfirm) {
            dtos.stream().filter(e -> e.getId().equals(eventsCount.getId()))
                    .forEach(e -> e.setConfirmedRequests(eventsCount.getCount()));
        }
        return dtos;
    }

    @Override
    @Transactional
    public Event updateEvent(long userId, long eventId, Event event, StateAction stateAction) {
        if (event.getEventDate() != null
                && !checkDiffTime(LocalDateTime.now(), event.getEventDate(), 2)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));

        Event eventUpd = eventRepository.findByIdAndInitiator(eventId, user);
        if (eventUpd == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        if (eventUpd.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        if (event.getAnnotation() != null && !event.getAnnotation().isBlank()) {
            eventUpd.setAnnotation(event.getAnnotation());
        }
        if (event.getTitle() != null && !event.getAnnotation().isBlank()) {
            eventUpd.setTitle(event.getTitle());
        }
        if (event.getDescription() != null && !event.getAnnotation().isBlank()) {
            eventUpd.setDescription(event.getDescription());
        }
        if (event.getCreatedOn() != null) {
            eventUpd.setCreatedOn(event.getCreatedOn());
        }

        if (event.getEventDate() != null) {
            eventUpd.setEventDate(event.getEventDate());
        }
        eventUpd = baseUpdateEvent(event, eventUpd);
        if (event.getPublishedOn() != null) {
            eventUpd.setPublishedOn(event.getPublishedOn());
        }
        eventUpd.setRequestModeration(event.getRequestModeration());
        if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
            eventUpd.setState(EventState.PENDING);
        } else if (event.getState() != null) {
            eventUpd.setState(event.getState());
        }
        return eventUpd;
    }

    private Event baseUpdateEvent(Event event, Event eventUpd) {
        if (event.getLatitude() != null) {
            eventUpd.setLatitude(event.getLatitude());
        }
        if (event.getLongitude() != null) {
            eventUpd.setLongitude(event.getLongitude());
        }
        if (event.getPaid() != null) {
            eventUpd.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            eventUpd.setParticipantLimit(event.getParticipantLimit());
        }
        return eventUpd;
    }


    @Override
    @Transactional
    public Event updateAdmin(long id, Event event) {

        Category category = new Category();
        if (event.getCategory().getId() != null) {
            category = categoryRepository.findById(event.getCategory().getId())
                    .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        }
        Event eventUpd = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (eventUpd == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        if (event.getAnnotation() != null && !event.getAnnotation().isBlank()) {
            eventUpd.setAnnotation(event.getAnnotation());
        }
        if (event.getTitle() != null && !event.getTitle().isBlank()) {
            eventUpd.setTitle(event.getTitle());
        }
        if (event.getDescription() != null && !event.getDescription().isBlank()) {
            eventUpd.setDescription(event.getDescription());
        }

        if (event.getEventDate() != null) {
            if (event.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ConflictException(HttpStatus.CONFLICT);
            }
            eventUpd.setEventDate(event.getEventDate());
        }
        eventUpd = baseUpdateEvent(event, eventUpd);
        eventUpd.setRequestModeration(event.getRequestModeration());
        if (event.getState() != null) {
            eventUpd.setState(event.getState());
        }
        if (event.getCategory() != null) {
            eventUpd.setCategory(category);
        }
        return eventUpd;
    }

    @Override
    public EventUserDto getUserEvent(long userId, long eventId) {
        EventUserDto dto = eventMapper.toUserDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
        dto.setViews(webClient.findView(eventId));
        dto.setConfirmedRequests(requestRepository.getCountConfirmed(eventId));
        return dto;
    }

    @Override
    @Transactional
    public Event cancelEvent(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        event.setState(EventState.CANCELED);
        return event;
    }

    private List<EventsCountConfirmed> getConfirmed(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String sql = "SELECT e.id id,COUNT(e.id) cnt FROM events e JOIN requests r ON r.event_id=e.id " +
                "WHERE r.status='CONFIRMED' AND e.state='PUBLISHED' AND e.id IN (:ids) GROUP BY e.id";

        List<EventsCountConfirmed> result = jdbcTemplate.query(sql, parameters, new RowMapper<EventsCountConfirmed>() {
            @Override
            public EventsCountConfirmed mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new EventsCountConfirmed(rs.getLong("id"), rs.getInt("cnt"));
            }
        });
        return result;
    }
}
