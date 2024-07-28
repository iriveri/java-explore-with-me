package ru.practicum.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticsService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatisticsService statisticsService;

    public PublicEventController(EventService eventService, StatisticsService statisticsService) {
        this.eventService = eventService;
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<EventShortDto> events = eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statisticsService.recordRequest("/events", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id) {
        EventFullDto event = eventService.getEvent(id);
        statisticsService.recordRequest("/events/" + id, 1);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}