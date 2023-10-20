package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.participation.dto.ParticipationRequestDto;
import ru.practicum.service.event.PrivateEventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateEventController {

    private final PrivateEventService service;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable Integer userId,
                                  @Valid @RequestBody NewEventDto eventDto) {
        log.info(String.format("Received POST event request. user id: {%d} event: {%s}", userId, eventDto));
        return service.postEvent(userId, eventDto);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsById(@PathVariable Integer userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info(String.format("Received GET events request. user id: {%d} from: {%d} size: {%d}", userId, from, size));
        return service.getEventsById(userId, from, size);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postParticipation(@PathVariable Integer userId,
                                                     @RequestParam Integer eventId) {
        log.info(String.format("Received POST participation request. user id: {%d} event id: {%d}", userId, eventId));
        return service.postParticipation(userId, eventId);
    }

}