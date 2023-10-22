package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.BaseClient;
import ru.practicum.HitResponseDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.event.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final BaseClient statClient;

    private final EventRepository eventRepository;

    private final ParticipationRepository participationRepository;

    private final CompilationRepository repository;

    @Override
    public CompilationDto postCompilation(NewCompilationDto dto) {
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            Compilation compilation = new Compilation(events, dto.isPinned(), dto.getTitle());
            List<EventShortDto> eventShort = events.stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                            getConfirmedRequests(),
                            UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                            getViews(event.getId())))
                    .collect(Collectors.toList());
            repository.save(compilation);
            return CompilationMapper.toCompilationDto(eventShort, compilation);
        }
        Compilation compilation = new Compilation(new ArrayList<>(), dto.isPinned(), dto.getTitle());

        return CompilationMapper.toCompilationDto(new ArrayList<>(), compilation);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        Optional<Compilation> compOpt = repository.findById(compId);
        if (compOpt.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id %d not found", compId));
        }
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto patchCompilation(Integer compId, UpdateCompilationRequest dto) {
        Optional<Compilation> compOpt = repository.findById(compId);
        if (compOpt.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id %d not found", compId));
        }
        Compilation compilation = compOpt.get();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            List<EventShortDto> eventShort = events.stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                            getConfirmedRequests(),
                            UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                            getViews(event.getId())))
                    .collect(Collectors.toList());
            compilation.setEvents(events);
            compilation.setPinned(dto.isPinned());
            compilation.setTitle(dto.getTitle());
            repository.save(compilation);
            return CompilationMapper.toCompilationDto(eventShort, compilation);
        }
        compilation.setEvents(new ArrayList<>());
        compilation.setPinned(dto.isPinned());
        compilation.setTitle(dto.getTitle());
        repository.save(compilation);
        return CompilationMapper.toCompilationDto(new ArrayList<>(), compilation);
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }

    private long getViews(Long eventId) {
        String uri = "events/" + eventId;
        List<HitResponseDto> viewsList;
        try {
            viewsList = statClient.getStats(LocalDateTime.now().minusYears(300), LocalDateTime.now().plusYears(300), List.of(uri), false);
        } catch (Exception e) {
            viewsList = Collections.emptyList();
        }
        HitResponseDto view = viewsList.isEmpty() ? new HitResponseDto("ewm-main-service", uri, 0L) : viewsList.get(0);

        return view.getHits();
    }

}