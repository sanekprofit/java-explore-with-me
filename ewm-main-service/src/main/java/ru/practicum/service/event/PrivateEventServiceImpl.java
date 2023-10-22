package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.BaseClient;
import ru.practicum.HitResponseDto;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.exceptions.ConflictParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventUserRequest;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.participation.dto.ParticipationRequestDto;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserShortDto;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.event.ParticipationRepository;
import ru.practicum.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final BaseClient statClient;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ParticipationRepository participationRepository;

    private final EventRepository repository;

    @Override
    public EventFullDto postEvent(Integer userId, NewEventDto eventDto) {

        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictParamException("For the requested operation the event date are not met.");
        }

        if (eventDto.getCategory() < 1) {
            throw new BadParamException("Category id must be above 0.");
        }

        Event event = EventMapper.toEvent(eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getEventDate(),
                eventDto.isPaid(), eventDto.getLocation().getLat(), eventDto.getLocation().getLon(),
                eventDto.getParticipantLimit(), eventDto.isRequestModeration(), eventDto.getTitle());

        User initiator = getUserById(userId);
        Category category = getCategoryById(eventDto.getCategory());

        event.setInitiator(initiator);
        event.setCategory(category);

        repository.save(event);

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category.getId(), category.getName());
        UserShortDto userShortDto = UserMapper.toUserShortDto(initiator.getId(), initiator.getName());

        return EventMapper.toEventFullDto(event.getAnnotation(), categoryDto, getConfirmedRequests(), event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), userShortDto,
                new Location(event.getLatitude(), event.getLongitude()), event.isPaid(), event.getParticipantLimit(),
                event.isRequestModeration(), event.getState(), event.getTitle(), getViews(event.getId()));
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Integer userId, int from, int size) {
        List<EventShortDto> dtos = new ArrayList<>();

        List<Event> events = repository.findAllByInitiator_Id(userId, PageRequest.of(from, size));

        if (!events.isEmpty()) {
            for (Event event : events) {
                CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName());
                UserShortDto userShortDto = UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName());

                EventShortDto eventShortDto = EventMapper.toEventShortDto(event.getAnnotation(), categoryDto,
                        getConfirmedRequests(), event.getEventDate(), event.getId(), userShortDto, event.isPaid(),
                        event.getTitle(), getViews(event.getId()));

                dtos.add(eventShortDto);
            }
        }
        return dtos;
    }

    @Override
    public EventFullDto getEventById(Integer userId, Long eventId) {
        Optional<Event> eventOpt = repository.findFirstByIdAndInitiator_Id(eventId, userId);
        if (eventOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id %d and user id %d not found", eventId, userId));
        }

        Event event = eventOpt.get();

        Category category = event.getCategory();
        User initiator = event.getInitiator();

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category.getId(), category.getName());
        UserShortDto userShortDto = UserMapper.toUserShortDto(initiator.getId(), initiator.getName());

        return EventMapper.toEventFullDto(event.getAnnotation(), categoryDto, getConfirmedRequests(), event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), userShortDto,
                new Location(event.getLatitude(), event.getLongitude()), event.isPaid(), event.getParticipantLimit(),
                event.isRequestModeration(), event.getState(), event.getTitle(), getViews(event.getId()));
    }

    @Override
    public EventFullDto patchEvent(Integer userId, Long eventId, UpdateEventUserRequest eventDto) {

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictParamException("For the requested operation the event date are not met.");
        }

        Optional<Event> eventOpt = repository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id %d not found", eventId));
        }

        Event event = eventOpt.get();

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictParamException("You can only change canceled events or events in the state of waiting for moderation");
        }

        patch(event, eventDto);

        Category category = getCategoryById(eventDto.getCategory());
        User initiator = getUserById(userId);

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category.getId(), category.getName());
        UserShortDto userShortDto = UserMapper.toUserShortDto(initiator.getId(), initiator.getName());

        repository.save(event);

        return EventMapper.toEventFullDto(event.getAnnotation(), categoryDto, getConfirmedRequests(), event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), userShortDto,
                new Location(event.getLatitude(), event.getLongitude()), event.isPaid(), event.getParticipantLimit(),
                event.isRequestModeration(), event.getState(), event.getTitle(), getViews(event.getId()));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationsByEventId(Integer userId, Long eventId) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();

        List<Participation> participations = participationRepository.findAllByEvent_IdAndEvent_Initiator_Id(eventId, userId);

        if (!participations.isEmpty()) {
            for (Participation participation : participations) {
                ParticipationRequestDto dto = ParticipationMapper.toDto(participation.getCreated(),
                        participation.getEvent().getId(), participation.getId(), participation.getRequester().getId(), participation.getStatus());

                dtos.add(dto);
            }
        }
        return dtos;
    }

    @Override
    public List<ParticipationRequestDto> updateRequestStatus(Integer userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();

        List<Participation> participations = participationRepository.findAllByEvent_IdAndEvent_Initiator_Id(eventId, userId);

        if (!participations.isEmpty()) {
            for (Participation participation : participations) {
                participation.setStatus(dto.getStatus());

                participationRepository.save(participation);

                ParticipationRequestDto partDto = ParticipationMapper.toDto(participation.getCreated(),
                        participation.getEvent().getId(), participation.getId(), participation.getRequester().getId(), participation.getStatus());
                dtos.add(partDto);
            }
        }
        return dtos;
    }

    @Override
    public List<ParticipationRequestDto> getUserParticipations(Integer userId) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();

        List<Participation> participations = participationRepository.findAllByRequester_Id(userId);

        if (!participations.isEmpty()) {
            for (Participation participation : participations) {
                ParticipationRequestDto dto = ParticipationMapper.toDto(participation.getCreated(),
                        participation.getEvent().getId(), participation.getId(), participation.getRequester().getId(), participation.getStatus());

                dtos.add(dto);
            }
        }
        return dtos;
    }

    @Override
    public ParticipationRequestDto postParticipation(Integer userId, Long eventId) {

        Optional<Participation> participationAgain = participationRepository.findFirstByRequester_Id(userId);
        if (participationAgain.isPresent()) {
            throw new ConflictParamException("You cannot add a repeat request.");
        }

        Optional<Participation> participationInitiator = participationRepository.findFirstByEvent_Initiator_Id(userId);
        if (participationInitiator.isPresent()) {
            throw new ConflictParamException("The initiator of the event cannot add a request to participate in his event.");
        }

        Optional<Event> eventOpt = repository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id %d not found", eventId));
        }


        User user = getUserById(userId);
        Event event = eventOpt.get();

        if (event.getState().equals(EventState.CANCELED) || event.getState().equals(EventState.PENDING)) {
            throw new NotFoundException("The event has not been published yet.");
        }

        Participation participation = new Participation(event, user);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participation.setStatus(ParticipantState.CONFIRMED);
        }

        if (getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictParamException("Participation limit has reached.");
        }

        participationRepository.save(participation);

        return ParticipationMapper.toDto(participation.getCreated(), participation.getEvent().getId(),
                participation.getId(), participation.getRequester().getId(), participation.getStatus());
    }

    @Override
    public ParticipationRequestDto patchParticipation(Integer userId, Integer requestId) {
        Optional<Participation> participationOpt = participationRepository.findFirstByRequester_IdAndId(userId, requestId);
        if (participationOpt.isEmpty()) {
            throw new NotFoundException(String.format("Participation with id %d not found", requestId));
        }

        Participation participation = participationOpt.get();

        participation.setStatus(ParticipantState.REJECTED);

        participationRepository.save(participation);
        return ParticipationMapper.toDto(participation.getCreated(), participation.getEvent().getId(),
                participation.getId(), participation.getRequester().getId(), participation.getStatus());
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }

    private Category getCategoryById(Integer catId) {
        Optional<Category> categoryOpt = categoryRepository.findById(catId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        return categoryOpt.get();
    }

    private User getUserById(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        return userOpt.get();
    }

    private int getViews(Long eventId) {
        String uri = "events/" + eventId;
        List<HitResponseDto> viewsList;
        try {
            viewsList = statClient.getStats(LocalDateTime.now().minusYears(300), LocalDateTime.now().plusYears(300), List.of(uri), false);
        } catch (Exception e) {
            viewsList = Collections.emptyList();
        }
        HitResponseDto view = viewsList.isEmpty() ? new HitResponseDto("ewm-main-service", uri, 0L) : viewsList.get(0);

        return Math.toIntExact(view.getHits());
    }

    private void patch(Event event, UpdateEventUserRequest eventDto) {
        event.setEventDate(eventDto.getEventDate());
        if (eventDto.getAnnotation() != null) event.setAnnotation(eventDto.getAnnotation());
        if (eventDto.getCategory() != 0) event.setCategory(getCategoryById(eventDto.getCategory()));
        if (eventDto.getDescription() != null) event.setDescription(event.getDescription());
        if (eventDto.getEventDate() != null) event.setEventDate(eventDto.getEventDate());
        if (eventDto.getLocation() != null) {
            event.setLatitude(eventDto.getLocation().getLat());
            event.setLongitude(eventDto.getLocation().getLon());
        }
        event.setPaid(eventDto.isPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.isRequestModeration());
        if (eventDto.getTitle() != null) event.setTitle(eventDto.getTitle());
        event.setState(EventState.CANCELED);
    }

}