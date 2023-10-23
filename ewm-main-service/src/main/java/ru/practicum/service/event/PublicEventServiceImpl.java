package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.BaseClient;
import ru.practicum.HitDto;
import ru.practicum.HitResponseDto;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.event.EventStorage;
import ru.practicum.repository.event.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final BaseClient statClient;

    private final EventStorage storage;

    private final ParticipationRepository participationRepository;

    private final EventRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text,
                                         List<Integer> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         int from,
                                         int size,
                                         String ip) {
        List<Event> events = storage.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, from, size);

        if (text != null && text.equals("0")) {
            throw new BadParamException("Incorrect type of text");
        }
        if (categories != null && !categories.isEmpty()) {
            for (Integer integer : categories) {
                if (integer.equals(0)) throw new BadParamException("Incorrect type of categories");
            }
        }

        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            statClient.saveHit(new HitDto("ewm-main-service", "events/" + event.getId(), ip, LocalDateTime.now()));
            EventShortDto dto = EventMapper.toEventShortDto(event,
                    CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                    getConfirmedRequests(),
                    UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                    getViews(event.getId()));

            if (onlyAvailable) {
                if (dto.getConfirmedRequests() <= event.getParticipantLimit()) {
                    dtos.add(dto);
                }
            } else {
                dtos.add(dto);
            }
        }

        if (sort != null && sort.equals("EVENT_DATE")) {
            dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else if (sort != null && sort.equals("VIEWS")) {
            dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        return dtos;
    }

    @Override
    public EventFullDto getEvent(Long eventId, String ip) {
        Optional<Event> eventOpt = repository.findFirstByIdAndStateEquals(eventId, EventState.PUBLISHED);
        if (eventOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id %d not found", eventId));
        }

        Event event = eventOpt.get();

        statClient.saveHit(new HitDto("ewm-main-service", "events/" + event.getId(), ip, LocalDateTime.now()));

        return EventMapper.toEventFullDto(event,
                CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                getConfirmedRequests(),
                UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                getViews(eventId));
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }

    private Long getViews(Long eventId) {
        String uri = "events/" + eventId;
        List<HitResponseDto> viewsList;
        try {
            viewsList = statClient.getStats(LocalDateTime.now().minusYears(300), LocalDateTime.now().plusYears(300), List.of(uri), true);
        } catch (Exception e) {
            viewsList = Collections.emptyList();
        }
        HitResponseDto view = viewsList.isEmpty() ? new HitResponseDto("ewm-main-service", uri, 0L) : viewsList.get(0);

        return view.getHits();
    }


}