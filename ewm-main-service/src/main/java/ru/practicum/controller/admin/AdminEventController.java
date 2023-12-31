package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.admin.AdminEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService service;

    @GetMapping
    public List<EventFullDto> getEventsSearch(@RequestParam(required = false) List<Integer> users,
                                              @RequestParam(required = false) List<String> states,
                                              @RequestParam(required = false) List<Integer> categories,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @RequestParam(defaultValue = "0", required = false) int from,
                                              @RequestParam(defaultValue = "10", required = false) int size,
                                              HttpServletRequest request) {
        log.info(String.format("Received GET events search. users: %s states: %s categories: %s start: %s end: %s from: %d size: %d",
                users, states, categories, rangeStart, rangeEnd, from, size));
        return service.getEventsSearch(users, states, categories, rangeStart, rangeEnd, from, size, request.getRemoteAddr());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventAdminRequest dto) {
        log.info(String.format("Received PATCH event admin request. event id: {%d} dto: {%s}", eventId, dto));
        return service.patchEvent(eventId, dto);
    }

}