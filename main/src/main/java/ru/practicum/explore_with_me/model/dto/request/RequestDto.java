package ru.practicum.explore_with_me.model.dto.request;

import lombok.Data;
import ru.practicum.explore_with_me.model.Status;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private Long id;
    private LocalDateTime created;
    private Status status;
    private Long requester;
    private Long event;
}
