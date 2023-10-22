package ru.practicum.mapper;

import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEvent(String annotation, String description, LocalDateTime eventDate, boolean paid,
                                float latitude, float longitude, int participantLimit, boolean requestModeration, String title) {
        return new Event(annotation, description, eventDate, latitude, longitude, paid, participantLimit, requestModeration, title);
    }

    public static EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, int confirmedRequests, UserShortDto initiator, long views) {
        return new EventFullDto(event.getAnnotation(), categoryDto, confirmedRequests, event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), initiator,
                new Location(event.getLatitude(), event.getLongitude()),
                event.isPaid(), event.getParticipantLimit(), event.isRequestModeration(), event.getState(), event.getTitle(), views);
    }

    public static EventFullDto toEventFullDto(String annotation, CategoryDto categoryDto, int confirmedRequests, LocalDateTime createdOn,
                                              String description, LocalDateTime eventDate, int id, UserShortDto initiator,
                                              Location location, boolean paid, int participantLimit,
                                              boolean requestModeration, EventState state, String title, long views) {
        return new EventFullDto(annotation, categoryDto, confirmedRequests, createdOn, description, eventDate, id, initiator, location,
                paid, participantLimit, requestModeration, state, title, views);
    }

    public static EventShortDto toEventShortDto(String annotation, CategoryDto categoryDto, int confirmedRequests,
                                                LocalDateTime eventDate, int id, UserShortDto initiator, boolean paid,
                                                String title, long views) {
        return new EventShortDto(annotation, categoryDto, confirmedRequests, eventDate, id, initiator, paid, title, views);
    }

    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, int confirmedRequests, UserShortDto initiator, long views) {
        return new EventShortDto(event.getAnnotation(), categoryDto, confirmedRequests, event.getEventDate(),
                event.getId(), initiator, event.isPaid(), event.getTitle(), views);
    }
}