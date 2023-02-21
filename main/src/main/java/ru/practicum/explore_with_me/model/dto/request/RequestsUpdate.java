package ru.practicum.exploreWithMe.model.dto.request;

import lombok.Data;
import ru.practicum.exploreWithMe.model.Status;

import java.util.List;

@Data
public class RequestsUpdate {
    private final List<Long> requestIds;
    private final Status status;
}
