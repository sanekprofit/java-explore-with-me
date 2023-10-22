package ru.practicum.service.admin;

import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEventsSearch(List<Integer> users, List<String> states, List<Integer> categories, String start, String end, int from, int size, String ip);

    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest dto);

}