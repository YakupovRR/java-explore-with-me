package ru.practicum.explore_with_me.model.dto.compilation;

import lombok.Data;
import ru.practicum.explore_with_me.model.dto.event.EventDto;

import java.util.Set;

@Data
public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventDto> events;
}
