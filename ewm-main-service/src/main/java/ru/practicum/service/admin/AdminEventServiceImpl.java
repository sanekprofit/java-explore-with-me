package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.BaseClient;
import ru.practicum.HitResponseDto;
import ru.practicum.exceptions.ConflictParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.model.event.enums.AdminStateAction;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserShortDto;
import ru.practicum.model.utilities.SelfFormatter;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.event.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final CategoryRepository categoryRepository;

    private final ParticipationRepository participationRepository;

    private final BaseClient statClient;

    private final EventRepository repository;

    @Override
    public List<EventFullDto> getEventsSearch(List<Integer> users, List<String> states, List<Integer> categories, String start, String end, int from, int size) {
        List<Event> events = repository.findEventsByParameters(users, parseStates(states), categories, parseDateTime(start),
                parseDateTime(end), PageRequest.of(from, size));

        return events.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                        getConfirmedRequests(),
                        UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                        getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto patchEvent(Integer eventId, UpdateEventAdminRequest dto) {
        Optional<Event> eventOpt = repository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id %d not found.", eventId));
        }

        Event event = eventOpt.get();

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictParamException("For the requested operation the event date are not met.");
        }

        patch(event, dto);

        if (dto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
                throw new ConflictParamException("You can only change events in the state of waiting for moderation");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (dto.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictParamException("You can only deny event that is not published yet.");
            }
            event.setState(EventState.CANCELED);
        }

        Category category = event.getCategory();
        User initiator = event.getInitiator();

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category.getId(), category.getName());
        UserShortDto userShortDto = UserMapper.toUserShortDto(initiator.getId(), initiator.getName());

        repository.save(event);

        return EventMapper.toEventFullDto(event.getAnnotation(), categoryDto, getConfirmedRequests(), event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), userShortDto,
                new Location(event.getLatitude(), event.getLongitude()), event.isPaid(), event.getParticipantLimit(),
                event.isRequestModeration(), event.getState(), event.getTitle(), getViews(event));
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return dateTime == null ? null : LocalDateTime.parse(dateTime, SelfFormatter.FORMAT);
    }

    private List<EventState> parseStates(List<String> states) {
        if (states != null && !states.isEmpty()) {
            return states.stream()
                    .map(EventState::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    private Category getCategoryById(Integer catId) {
        Optional<Category> categoryOpt = categoryRepository.findById(catId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        return categoryOpt.get();
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }

    private long getViews(Event event) {
        String uri = "events/" + event.getId();
        List<HitResponseDto> viewsList;
        try {
            viewsList = statClient.getStats(LocalDateTime.now().minusYears(300), LocalDateTime.now().plusYears(300), List.of(uri), false);
        } catch (Exception e) {
            viewsList = Collections.emptyList();
        }
        HitResponseDto view = viewsList.isEmpty() ? new HitResponseDto("ewm-main-service", uri, 0L) : viewsList.get(0);

        return view.getHits();
    }

    private void patch(Event event, UpdateEventAdminRequest dto) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        event.setCategory(getCategoryById(dto.getCategory()));
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) {
            event.setLatitude(dto.getLocation().getLat());
            event.setLongitude(dto.getLocation().getLon());
        }
        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.isRequestModeration());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
    }
}