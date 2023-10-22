package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.repository.event.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCompilationServiceImpl implements UserCompilationService {

    private final BaseClient statClient;

    private final ParticipationRepository participationRepository;

    private final CompilationRepository repository;

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        List<CompilationDto> dtos = new ArrayList<>();

        List<Compilation> compilations = repository.findAllByPinned(pinned, PageRequest.of(from, size));
        if (compilations.isEmpty()) {
            throw new NotFoundException("Compilations with parameters not found.");
        }

        for (Compilation compilation : compilations) {
            List<EventShortDto> eventShort = compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                            getConfirmedRequests(),
                            UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                            getViews(event.getId())))
                    .collect(Collectors.toList());
            dtos.add(CompilationMapper.toCompilationDto(eventShort, compilation));
        }

        return dtos;
    }

    @Override
    public CompilationDto getCompilation(Integer compId) {
        Optional<Compilation> compOpt = repository.findById(compId);
        if (compOpt.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id %d not found.", compId));
        }
        Compilation compilation = compOpt.get();
        List<EventShortDto> eventShort = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        CategoryMapper.toCategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                        getConfirmedRequests(),
                        UserMapper.toUserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                        getViews(event.getId())))
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(eventShort, compilation);
    }

    private int getConfirmedRequests() {
        List<Participation> participations = participationRepository.findAllByStatusEquals(ParticipantState.CONFIRMED);
        return participations.size();
    }

    private long getViews(int eventId) {
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