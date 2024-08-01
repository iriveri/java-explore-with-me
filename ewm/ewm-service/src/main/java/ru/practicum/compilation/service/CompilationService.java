package ru.practicum.compilation.service;

import ru.practicum.compilation.Compilation;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {


    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);

    Compilation getCompilationById(Long compId);

    CompilationDto getCompilationDtoById(Long compId);

    List<CompilationDto> getCompilations(Optional<Boolean> pinned, int offset, int limit);
}
