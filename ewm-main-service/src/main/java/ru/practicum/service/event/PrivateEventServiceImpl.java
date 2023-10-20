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
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.participation.Participation;
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

        Optional<Category> categoryOpt = categoryRepository.findById(eventDto.getCategory());
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found.", eventDto.getCategory()));
        }

        Optional<User> initiatorOpt = userRepository.findById(userId);
        if (initiatorOpt.isEmpty()) {
            throw new NotFoundException(String.format("User with id %d not found.", userId));
        }

        Event event = EventMapper.toEvent(eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getEventDate(),
                eventDto.isPaid(), eventDto.getLocation().getLat(), eventDto.getLocation().getLon(),
                eventDto.getParticipantLimit(), eventDto.isRequestModeration(), eventDto.getTitle());

        User initiator = initiatorOpt.get();
        Category category = categoryOpt.get();

        event.setInitiator(initiator);
        event.setCategory(category);

        repository.save(event);

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category.getId(), category.getName());
        UserShortDto userShortDto = UserMapper.toUserShortDto(initiator.getId(), initiator.getName());

        return EventMapper.toEventFullDto(event.getAnnotation(), categoryDto, getConfirmedRequests(), event.getCreatedOn(),
                event.getDescription(), event.getEventDate(), event.getId(), userShortDto,
                new Location(event.getLatitude(), event.getLongitude()), event.isPaid(), event.getParticipantLimit(),
                event.isRequestModeration(), event.getState(), event.getTitle());
    }

    @Override
    public List<EventShortDto> getEventsById(Integer userId, int from, int size) {
        List<EventShortDto> dtos = new ArrayList<>();


        List<Event> events = repository.findAllByInitiator_Id(userId, PageRequest.of(from, size));

        if (!events.isEmpty()) {
            for (Event event : events) {
                List<String> uris = List.of("events/" + event.getId());
                List<HitResponseDto> viewsList = statClient.getStats(LocalDateTime.now(), LocalDateTime.now().plusYears(3), uris, false);
                HitResponseDto view;
                if (viewsList.isEmpty()) {
                    view = new HitResponseDto("ewm-main-service", "events/" + event.getId(), 0L);
                } else {
                    view = viewsList.get(0);
                }
                CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName());
                UserShortDto userShortDto = UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName());


                EventShortDto eventShortDto = EventMapper.toEventShortDto(event.getAnnotation(), categoryDto,
                        getConfirmedRequests(), event.getEventDate(), event.getId(), userShortDto, event.isPaid(),
                        event.getTitle(), view.getHits());

                dtos.add(eventShortDto);
            }
        }
        return dtos;
    }

    @Override
    public ParticipationRequestDto postParticipation(Integer userId, Integer eventId) {

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("User with id %d not found.", userId));
        }

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


        User user = userOpt.get();
        Event event = eventOpt.get();

        if (event.getState().equals(EventState.CANCELED) || event.getState().equals(EventState.PENDING)) {
            throw new ConflictParamException("The event has not been published yet.");
        }

        if (getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictParamException("Participation limit has reached.");
        }

        Participation participation = new Participation(event, user);

        if (!event.isRequestModeration()) {
            participation.setStatus(ParticipantState.CONFIRMED);
            participationRepository.save(participation);
        } else {
            participationRepository.save(participation);
        }

        return ParticipationMapper.toDto(participation.getCreated(), participation.getEvent().getId(),
                participation.getId(), participation.getRequester().getId(), participation.getStatus());
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }
}