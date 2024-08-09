package ru.practicum.compilation.service;

import ru.practicum.compilation.Compilation;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {


    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationDto updateCompilationDto);

    void delete(Long compId);

    Compilation getEntityById(Long compId);

    CompilationDto getById(Long compId);

    List<CompilationDto> getAll(Optional<Boolean> pinned, int offset, int limit);
}
