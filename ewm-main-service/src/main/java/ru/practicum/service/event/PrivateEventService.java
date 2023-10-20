package ru.practicum.service.event;

import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto postEvent(Integer userId, NewEventDto eventDto);

    List<EventShortDto> getEventsById(Integer userId, int from, int size);

    ParticipationRequestDto postParticipation(Integer userId, Integer eventId);

}