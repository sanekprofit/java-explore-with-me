package ru.practicum.service.user;

import ru.practicum.model.compilation.dto.CompilationDto;

import java.util.List;

public interface UserCompilationService {

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilation(Integer compId);

}