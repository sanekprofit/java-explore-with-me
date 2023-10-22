package ru.practicum.service.event;

import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(String text,
                                  List<Integer> categories,
                                  boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  boolean onlyAvailable,
                                  String sort,
                                  int from,
                                  int size,
                                  String ip);

    EventFullDto getEvent(Long eventId, String ip);

}