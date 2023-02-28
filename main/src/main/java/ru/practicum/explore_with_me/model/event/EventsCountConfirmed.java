package ru.practicum.explore_with_me.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventsCountConfirmed {
    private Long id;
    private Integer count;

}
