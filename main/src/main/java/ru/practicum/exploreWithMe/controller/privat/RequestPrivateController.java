package ru.practicum.exploreWithMe.controller.privat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.model.Status;
import ru.practicum.exploreWithMe.model.dto.request.RequestDto;
import ru.practicum.exploreWithMe.model.dto.request.RequestsUpdate;
import ru.practicum.exploreWithMe.model.dto.request.RequestsUpdResult;
import ru.practicum.exploreWithMe.model.mapper.RequestMapper;
import ru.practicum.exploreWithMe.service.request.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<RequestDto> addRequest(@PathVariable long userId, @RequestParam @Valid @NotNull long eventId) {
        log.info("Получен Post запрос к эндпоинту users" + userId + "/requests");
        RequestDto result = requestMapper.toDto(requestService.addRequest(userId, eventId));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getAll(@PathVariable long userId,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен Get запрос к эндпоинту users" + userId + "/requests");
        return requestService.getAllRequests(userId, from, size).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Получен Post запрос к эндпоинту users" + userId + "/requests/" + requestId + "/cansel");
        return requestMapper.toDto(requestService.cancelRequest(userId, requestId));
    }


    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getAllRequestsOfAuthor(@PathVariable long userId, @PathVariable long eventId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен Post запрос к эндпоинту users" + userId + "/events/" + eventId + "/requests");
        return requestService.getAllRequestsOfAuthor(userId, eventId, from, size).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestsUpdResult update(@PathVariable long userId, @PathVariable long eventId,
                                    @RequestBody RequestsUpdate requestsUpdate) {
        log.info("Получен Patch запрос к эндпоинту users" + userId + "/events/" + eventId + "/requests");
        if (requestsUpdate.getStatus().equals(Status.REJECTED)) {
            return requestService.rejectRequest(userId, eventId, requestsUpdate);
        } else {
            return requestService.updateRequest(userId, eventId, requestsUpdate);
        }
    }
}
