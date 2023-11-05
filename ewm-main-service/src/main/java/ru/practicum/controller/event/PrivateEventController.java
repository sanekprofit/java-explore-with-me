package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.participation.dto.EventRequestStatusUpdateResult;
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

    private static final String COMMENT = "/comments";

    private static final String COMENTID = "/comments/{commentId}";

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable Integer userId,
                                  @Valid @RequestBody NewEventDto eventDto) {
        log.info(String.format("Received POST event request. user id: {%d} event: {%s}", userId, eventDto));
        return service.postEvent(userId, eventDto);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable Integer userId,
                                             @RequestParam(defaultValue = "0", required = false) int from,
                                             @RequestParam(defaultValue = "10", required = false) int size) {
        log.info(String.format("Received GET events request. user id: {%d} from: {%d} size: {%d}", userId, from, size));
        return service.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer userId,
                                     @PathVariable Long eventId) {
        log.info(String.format("Received GET event request. user id: {%d}, event id: {%d}", userId, eventId));
        return service.getEventById(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable Integer userId,
                                   @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.info(String.format("Received PATCH events request. user id: {%d} event id: {%d} dto: {%s}", userId, eventId, eventDto));
        return service.patchEvent(userId, eventId, eventDto);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationsByEventId(@PathVariable Integer userId,
                                                              @PathVariable Long eventId) {
        log.info(String.format("Received GET participations by event id. user id: {%d} event id: {%d}", userId, eventId));
        return service.getParticipationsByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Integer userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info(String.format("Received PATCH request status. user id: {%d} event id: {%d} dto: {%s}", userId, eventId, dto));
        return service.updateRequestStatus(userId, eventId, dto);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserParticipations(@PathVariable Integer userId) {
        log.info(String.format("Received GET user participations request. user id: {%d}", userId));
        return service.getUserParticipations(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postParticipation(@PathVariable Integer userId,
                                                     @RequestParam Long eventId) {
        log.info(String.format("Received POST participation request. user id: {%d} event id: {%d}", userId, eventId));
        return service.postParticipation(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto patchParticipation(@PathVariable Integer userId,
                                                      @PathVariable Integer requestId) {
        log.info(String.format("Received PATCH participation request. user id: {%d} request id: {%d}", userId, requestId));
        return service.patchParticipation(userId, requestId);
    }

    @PostMapping(COMMENT)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable Integer userId,
                                  @RequestParam Long eventId,
                                  @Valid @RequestBody NewCommentDto dto) {
        log.info(String.format("Received POST comment request. user id: {%d} event id: {%d} dto: {%s}", userId, eventId, dto));
        return service.postComment(userId, eventId, dto);
    }

    @GetMapping(COMMENT)
    public List<CommentDto> getCommentsByEventId(@PathVariable Integer userId,
                                        @RequestParam Long eventId) {
        log.info(String.format("Received GET comments request. user id: {%d} event id: {%d}", userId, eventId));
        return service.getCommentsByEventId(userId, eventId);
    }

    @GetMapping(COMENTID)
    public CommentDto getCommentById(@PathVariable Integer userId,
                                     @PathVariable Long commentId) {
        log.info(String.format("Received GET comment request. user id: {%d} comment id : {%d}", userId, commentId));
        return service.getCommentById(userId, commentId);
    }

    @PatchMapping(COMENTID)
    public CommentDto patchComment(@PathVariable Integer userId,
                                   @PathVariable Long commentId,
                                   @Valid @RequestBody NewCommentDto dto) {
        log.info(String.format("Received POST comment request. user id: {%d} comment id: {%d} dto: {%s}", userId, commentId, dto));
        return service.patchComment(userId, commentId, dto);
    }
}