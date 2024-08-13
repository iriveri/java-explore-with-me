package ru.practicum.event.service;

import ru.practicum.dto.event.*;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.event.user.UserUpdateEventRequest;
import ru.practicum.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto create(Long userId, NewEventDto newEventDto);

    EventDto update(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventDto update(Long userId, Long eventId, UserUpdateEventRequest userUpdateEventRequest);

    Event getEntityById(Long eventId);

    EventDto getById(Long eventId);

    EventDto getById(Long userId, Long eventId);

    List<EventShortDto> getByUserId(Long userId, int from, int size);

    List<EventDto> getAll(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortOption sort, int from, int size);

}
