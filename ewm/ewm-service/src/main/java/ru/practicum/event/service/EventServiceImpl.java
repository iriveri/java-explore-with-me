package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.EventSort;
import ru.practicum.dto.EventState;
import ru.practicum.dto.event.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }


    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        return List.of();
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        return null;
    }


    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size) {
        return null;
    }


    @Override
    public EventFullDto getEvent(Long id) {
        return null;
    }
}
