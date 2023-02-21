package ru.practicum.explore_with_me.model.dto.request;

import lombok.Data;
import ru.practicum.explore_with_me.model.Status;

import java.util.List;

@Data
public class RequestsUpdate {
    private final List<Long> requestIds;
    private final Status status;
}
