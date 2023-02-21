package ru.practicum.explore_with_me.model.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore_with_me.model.StateAction;
import ru.practicum.explore_with_me.model.dto.LocationDto;

import java.time.LocalDateTime;

@Data
public class EventAdminDto {
    private String annotation;
    private String title;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Long category;
    private StateAction stateAction;
}
