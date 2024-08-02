package ru.practicum.event.service;

import ru.practicum.dto.event.EventSort;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.*;
import ru.practicum.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto update(Long eventId, UpdateEventAdminDto updateEventAdminDto);


    EventFullDto update(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    Event getEntityById(Long eventId);
    EventFullDto getById(Long eventId);
    EventFullDto getById(Long userId, Long eventId);

    List<EventShortDto> getByUserId(Long userId, int from, int size);
    List<EventFullDto> getAll(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size);



}
