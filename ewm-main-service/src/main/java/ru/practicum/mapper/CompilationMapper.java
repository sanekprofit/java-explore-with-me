package ru.practicum.mapper;

import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.event.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(List<EventShortDto> events, Compilation compilation) {
        return new CompilationDto(events, compilation.getId(), compilation.isPinned(), compilation.getTitle());
    }
}