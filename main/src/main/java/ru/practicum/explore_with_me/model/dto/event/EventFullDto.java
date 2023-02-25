package ru.practicum.explore_with_me.model.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import ru.practicum.explore_with_me.model.dto.CategoryDto;
import ru.practicum.explore_with_me.model.dto.LocationDto;
import ru.practicum.explore_with_me.model.dto.UserDto;
import ru.practicum.explore_with_me.model.event.EventState;

import java.time.LocalDateTime;

@Data
@Getter
public class EventFullDto {
    private Long id;
    private String annotation;
    private String title;
    private String description;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private CategoryDto category;
    private UserDto initiator;
    private Integer views;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;


}
