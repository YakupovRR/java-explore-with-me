package ru.practicum.exploreWithMe.model.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.exploreWithMe.model.StateAction;
import ru.practicum.exploreWithMe.model.dto.LocationDto;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
public class EventUpdDto {

    private Long eventId;
    private String annotation;
    private String title;
    private String description;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    private Long category;
    private StateAction stateAction;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @PositiveOrZero
    private Integer participantLimit;
}
