package ru.practicum.model.participation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {

    ParticipationRequestDto confirmedRequests;

    ParticipationRequestDto rejectedRequests;

}