package ru.practicum.event;

import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        return null;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto getEvent(Long id) {
        return null;
    }
}
