package ru.practicum.service.event;

import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto postEvent(Integer userId, NewEventDto eventDto);

    List<EventShortDto> getEventsByUserId(Integer userId, int from, int size);

    EventFullDto getEventById(Integer userId, Long eventId);

    EventFullDto patchEvent(Integer userId, Long eventId, UpdateEventUserRequest eventDto);

    List<ParticipationRequestDto> getParticipationsByEventId(Integer userId, Long eventId);

    List<ParticipationRequestDto> updateRequestStatus(Integer userId, Long eventId, EventRequestStatusUpdateRequest dto);

    List<ParticipationRequestDto> getUserParticipations(Integer userId);

    ParticipationRequestDto postParticipation(Integer userId, Long eventId);

    ParticipationRequestDto patchParticipation(Integer userId, Integer requestId);

}