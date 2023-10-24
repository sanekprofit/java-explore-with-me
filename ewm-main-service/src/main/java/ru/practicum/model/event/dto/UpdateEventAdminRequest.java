package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.enums.AdminStateAction;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {

    @Size(max = 2000, min = 20)
    String annotation;

    int category;

    @Size(max = 7000, min = 20)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    int participantLimit;

    Boolean requestModeration;

    AdminStateAction stateAction;

    @Size(max = 120, min = 3)
    String title;
}
