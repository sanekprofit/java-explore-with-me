package ru.practicum.model.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "created")
    LocalDateTime createdOn = LocalDateTime.now();

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    float latitude;

    float longitude;

    boolean paid;

    int participantLimit;

    @Column(name = "published")
    LocalDateTime publishedOn;

    @Column(name = "moderation")
    boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state = EventState.PENDING;

    String title;

    public Event(String annotation, String description, LocalDateTime eventDate, float latitude, float longitude,
                 boolean paid, int participantLimit, boolean requestModeration, String title) {
        this.annotation = annotation;
        this.description = description;
        this.eventDate = eventDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }
}