package ru.practicum.compilation.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService, CompilationMapper compilationMapper) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
        this.compilationMapper = compilationMapper;
    }


    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation newCompilation = compilationMapper.fromDto(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            List<Event> events = newCompilationDto.getEvents().stream()
                    .map(eventService::getEntityById)
                    .collect(Collectors.toList());
            newCompilation.setEvents(events);
        }

        return compilationMapper.toDto(compilationRepository.save(newCompilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compilationId, UpdateCompilationDto compilationDto) {
        Compilation compilation = getEntityById(compilationId);

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getEvents() != null) {
            List<Event> events = compilationDto.getEvents().stream()
                    .map(eventService::getEntityById)
                    .collect(Collectors.toList());
            compilation.setEvents(events);
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compilationId) {
        if (!compilationRepository.existsById(compilationId))
            throw new NotFoundException("Compilation with id=" + compilationId + " was not found");

        compilationRepository.deleteById(compilationId);
    }


    @Override
    public Compilation getEntityById(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compilationId + " was not found"));
    }

    @Override
    public CompilationDto getById(Long compilationId) {
        Compilation compilation = getEntityById(compilationId);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Optional<Boolean> pinned, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        Page<Compilation> compilations;

        if (pinned.isPresent()) {
            compilations = compilationRepository.findByPinned(pinned.get(), pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest);
        }

        return compilations.getContent().stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

}


