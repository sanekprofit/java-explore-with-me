package ru.practicum.service.admin;

import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto postCompilation(NewCompilationDto dto);

    void deleteCompilation(Integer compId);

    CompilationDto patchCompilation(Integer compId, UpdateCompilationRequest dto);

}