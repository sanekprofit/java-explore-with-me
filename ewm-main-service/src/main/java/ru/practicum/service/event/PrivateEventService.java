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

    EventFullDto getEventById(Integer userId, Integer eventId);

    EventFullDto patchEvent(Integer userId, Integer eventId, UpdateEventUserRequest eventDto);

    List<ParticipationRequestDto> getParticipationsByEventId(Integer userId, Integer eventId);

    List<ParticipationRequestDto> updateRequestStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest dto);

    List<ParticipationRequestDto> getUserParticipations(Integer userId);

    ParticipationRequestDto postParticipation(Integer userId, Integer eventId);

    ParticipationRequestDto patchParticipation(Integer userId, Integer requestId);

}