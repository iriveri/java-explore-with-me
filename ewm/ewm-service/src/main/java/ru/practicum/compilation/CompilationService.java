package ru.practicum.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);
}
