package ru.practicum.exploreWithMe.model.event;

import lombok.Data;

@Data
public class EventsCountConfirmed {
    private Long id;
    private Integer count;

    public EventsCountConfirmed(Long id, Integer count) {
        this.id = id;
        this.count = count;
    }
}
