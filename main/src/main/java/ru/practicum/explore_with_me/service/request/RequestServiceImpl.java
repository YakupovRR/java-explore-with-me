package ru.practicum.explore_with_me.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.model.*;
import ru.practicum.explore_with_me.model.dto.request.RequestsUpdate;
import ru.practicum.explore_with_me.model.dto.request.RequestsUpdResult;
import ru.practicum.explore_with_me.model.event.Event;
import ru.practicum.explore_with_me.model.event.EventState;
import ru.practicum.explore_with_me.model.mapper.RequestMapper;
import ru.practicum.explore_with_me.repository.EventRepository;
import ru.practicum.explore_with_me.repository.RequestRepository;
import ru.practicum.explore_with_me.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public Request addRequest(long userId, long eventId) {

        Request requestExist = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requestExist != null) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now().withNano(0));
        request.setRequester(user);
        request.setEvent(event);
        if (requestRepository.getCountConfirmed(eventId) >= event.getParticipantLimit()
                && event.getParticipantLimit() > 0) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public Request cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId);
        request.setStatus(Status.CANCELED);
        return request;
    }

    @Override
    public List<Request> getAllRequests(long requesterId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return requestRepository.findAllByRequesterOrderByCreated(requesterId, pageable);
    }

    @Override
    public List<Request> getAllRequestsOfAuthor(long userId, long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        return requestRepository.findAllByEventId(eventId, pageable);
    }

    @Override
    @Transactional
    public RequestsUpdResult updateRequest(long userId, long eventId, RequestsUpdate requestsUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
        Integer countConfirmed = requestRepository.getCountConfirmed(eventId);
        List<Request> requests = requestRepository.findAllByEventIdAndStatus(eventId, Status.PENDING);
        if (event.getParticipantLimit() <= countConfirmed) {
            requests.forEach(r -> r.setStatus(Status.REJECTED));
            throw new ConflictException(HttpStatus.CONFLICT);
        } else if ((event.getParticipantLimit() - countConfirmed) >= requestsUpdate.getRequestIds().size()) {
            requests.forEach(r -> r.setStatus(Status.CONFIRMED));
        }
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> confirmedRequests = requestRepository.findAllByIds(requestsUpdate.getRequestIds());
        confirmedRequests.forEach(request -> request.setStatus(requestsUpdate.getStatus()));
        return requestMapper.toReqUpdRes(confirmedRequests, rejectedRequests);
    }

    @Override
    @Transactional
    public RequestsUpdResult rejectRequest(long userId, long eventId, RequestsUpdate requestsUpdate) {
               Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(HttpStatus.CONFLICT);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = requestRepository.findAllByIds(requestsUpdate.getRequestIds());

        for (Request request : rejectedRequests) {
            if (request.getStatus().equals(Status.CONFIRMED)) {
                throw new ConflictException(HttpStatus.CONFLICT);
            }
            request.setStatus(requestsUpdate.getStatus());
        }
        return requestMapper.toReqUpdRes(confirmedRequests, rejectedRequests);
    }
}
