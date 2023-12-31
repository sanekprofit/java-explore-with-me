package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.service.event.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final PublicEventService service;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Integer> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false", required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0", required = false) int from,
                                         @RequestParam(defaultValue = "10", required = false) int size,
                                         HttpServletRequest request) {
        log.info(String.format("Received GET public events request. text: {%s} categories : %s paid : {%b} start : {%s} " +
                        "end: {%s} onlyAvailable: {%b} sort: {%s} from: {%d} size: {%d}",
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size));
        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info(String.format("Received GET public event request. event id: {%d}", id));
        return service.getEvent(id, request.getRemoteAddr());
    }

}