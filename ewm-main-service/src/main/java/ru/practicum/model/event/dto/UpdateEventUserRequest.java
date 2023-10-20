package ru.practicum.model.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.enums.UserStateAction;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    String annotation;

    int category;

    String description;

    LocalDateTime eventDate;

    Location location;

    boolean paid;

    int participantLimit;

    boolean requestModeration;

    UserStateAction stateAction;

    String title;
}