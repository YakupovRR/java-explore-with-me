package ru.practicum.explore_with_me.service.request;

import ru.practicum.explore_with_me.model.Request;
import ru.practicum.explore_with_me.model.dto.request.RequestsUpdate;
import ru.practicum.explore_with_me.model.dto.request.RequestsUpdResult;

import java.util.List;

public interface RequestService {

    Request addRequest(long userId, long eventId);

    Request cancelRequest(long userId, long requestId);

    List<Request> getAllRequests(long requesterId, Integer from, Integer size);

    List<Request> getAllRequestsOfAuthor(long userId, long eventId, Integer from, Integer size);

    RequestsUpdResult updateRequest(long userId, long eventId, RequestsUpdate requestsUpdate);

    RequestsUpdResult rejectRequest(long userId, long eventId, RequestsUpdate upd);
}
