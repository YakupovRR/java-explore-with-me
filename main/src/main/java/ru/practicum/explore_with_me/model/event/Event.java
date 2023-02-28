package ru.practicum.explore_with_me.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore_with_me.model.Category;
import ru.practicum.explore_with_me.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 7000)
    private String description;

    @Column(name = "created")
    private LocalDateTime createdOn;

    @Column(name = "dt", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "event_lat", nullable = false)
    private Double latitude;

    @Column(name = "event_lon", nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Boolean paid;

    @Column(name = "participant_max", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_dt")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EventState state;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private User initiator;

}
