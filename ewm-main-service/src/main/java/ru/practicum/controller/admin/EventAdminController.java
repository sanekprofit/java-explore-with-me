package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.admin.AdminEventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final AdminEventService service;

    @GetMapping
    public List<EventFullDto> getEventsSearch(@RequestParam List<Integer> users,
                                              @RequestParam List<String> states,
                                              @RequestParam List<Integer> categories,
                                              @RequestParam String rangeStart,
                                              @RequestParam String rangeEnd,
                                              @RequestParam(defaultValue = "0", required = false) int from,
                                              @RequestParam(defaultValue = "10", required = false) int size) {
        log.info(String.format("Received GET events search. users: %s states: %s categories: %s start: %s end: %s from: %d size: %d",
                users, states, categories, rangeStart, rangeEnd, from, size));
        return service.getEventsSearch(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable Long eventId,
                                   @RequestBody UpdateEventAdminRequest dto) {
        log.info(String.format("Received PATCH event admin request. event id: {%d} dto: {%s}", eventId, dto));
        return service.patchEvent(eventId, dto);
    }

}