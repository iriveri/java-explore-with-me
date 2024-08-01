package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepo;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.event.EventRepo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepo repo;
    private final EventRepo eventRepo ;
    private final CompilationMapper mapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepo repo, EventRepo eventRepo, CompilationMapper mapper) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.mapper = mapper;
    }


    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.fromDto(newCompilationDto);
        var savedCompilation = repo.save(compilation);
        eventRepo.saveEvents(newCompilationDto.getEvents());

        return getCompilationDtoById(savedCompilation.getId());
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = getCompilationById(compId);
        compilation.setTitle(updateCompilationDto.getTitle());
        compilation.setPinned(updateCompilationDto.getPinned());
        var savedCompilation = repo.save(compilation);
        return getCompilationDtoById(savedCompilation.getId());
    }

    @Override
    public void deleteCompilation(Long compId) {
        repo.deleteById(compId);
    }


    @Override
    public Compilation getCompilationById(Long compId) {
        Optional<Compilation> category = repo.findById(compId);
        if (category.isEmpty())
            throw new RuntimeException();

        return category.get();
    }
    @Override
    public CompilationDto getCompilationDtoById(Long compId) {
        Compilation compilation = getCompilationById(compId);
        CompilationDto compilationDto =  mapper.toDto(compilation);
        compilationDto.setEvents(eventRepo.getCompilationRepo(compId));

        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return repo.findAll(pageRequest).getContent()
                .stream()
                .map(compilation -> getCompilationDtoById(compilation.getId()))
                .collect(Collectors.toList());
    }

}


