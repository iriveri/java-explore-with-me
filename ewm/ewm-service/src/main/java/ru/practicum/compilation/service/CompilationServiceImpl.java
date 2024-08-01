package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.NotFoundException;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepo;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepo repo;
    private final EventService eventService;
    private final CompilationMapper mapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepo repo, EventService eventService, CompilationMapper mapper) {
        this.repo = repo;
        this.eventService = eventService;
        this.mapper = mapper;
    }


    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.fromDto(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            List<Event> events = newCompilationDto.getEvents().stream()
                    .map(eventService::getEntityById)
                    .collect(Collectors.toList());
            compilation.setEvents(events);
        }

        return mapper.toDto(repo.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = getEntityById(compId);

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }

        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = updateCompilationDto.getEvents().stream()
                    .map(eventService::getEntityById)
                    .collect(Collectors.toList());
            compilation.setEvents(events);
        }

        return mapper.toDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        if (!repo.existsById(compId))
            throw new NotFoundException("Compilation with id=" + compId + " was not found");

        repo.deleteById(compId);
    }


    @Override
    public Compilation getEntityById(Long compId) {
        return repo.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " was not found"));
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = getEntityById(compId);
        return mapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Optional<Boolean> pinned, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        Page<Compilation> compilations;

        if (pinned.isPresent()) {
            compilations = repo.findByPinned(pinned.get(), pageRequest);
        } else {
            compilations = repo.findAll(pageRequest);
        }

        return compilations.getContent().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

}


