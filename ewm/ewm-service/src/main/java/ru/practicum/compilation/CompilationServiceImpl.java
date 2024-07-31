package ru.practicum.compilation;

import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

@Service
public class CompilationServiceImpl implements CompilationService {
    @Override
    public CompilationDto getCompilationById(Long compId) {
        return null;
    }

    @Override
    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size) {
        return List.of();
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        return null;
    }

    @Override
    public void deleteCompilation(Long compId) {

    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        return null;
    }
}
