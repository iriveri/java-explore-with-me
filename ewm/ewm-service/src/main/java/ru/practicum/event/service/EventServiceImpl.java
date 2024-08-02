package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ConditionNotMetException;
import ru.practicum.NotFoundException;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.*;
import ru.practicum.dto.event.EventSort;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.*;
import ru.practicum.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepo repo;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(EventRepo eventRepository, UserService userService,
                            CategoryService categoryService, EventMapper eventMapper) {
        this.repo = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.fromDto(newEventDto);
        event.setInitiator(userService.getEntityById(userId));
        event.setCategory(categoryService.getEntityById(newEventDto.getCategory()));

        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(null);
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return eventMapper.toDto(repo.save(event));
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminDto updateEvent) {
        Event event = getEntityById(eventId);

        if (updateEvent.getEventDate() != null &&
                event.getPublishedOn() != null &&
                updateEvent.getEventDate().isBefore(event.getPublishedOn().plusHours(1)))
            throw new ConditionNotMetException("Event date must be at least one hour after the publication date.");

        // Проверка состояния события для публикации
        if (AdminStateAction.PUBLISH_EVENT.equals(updateEvent.getStateAction()) &&
                                !EventState.PENDING.equals(event.getState()))
            throw new ConditionNotMetException("Event can only be published if it is in pending state.");

        // Проверка состояния события для отклонения
        if (AdminStateAction.REJECT_EVENT.equals(updateEvent.getStateAction()) &&
                              EventState.PUBLISHED.equals(event.getState()))
            throw new ConditionNotMetException("Event can only be rejected if it is not published.");

        eventMapper.updateEventFromAdminDto(updateEvent, event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserDto updateEvent) {
        Event event = getEntityById(eventId);

        if (!event.getInitiator().getId().equals(userId))
            throw new ConditionNotMetException("User is not the initiator of the event");


        // Проверка состояния события для пользователя
        EventState eventState = event.getState();
        if (!EventState.CANCELED.equals(eventState) && !EventState.PENDING.equals(eventState))
            throw new ConditionNotMetException("Only pending or canceled events can be changed.");


        // Проверка даты и времени события
        if (updateEvent.getEventDate() != null &&
                updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConditionNotMetException("Event date must be at least two hours from the current time.");

        eventMapper.updateEventFromUserDto(updateEvent, event);
        return eventMapper.toDto(event);
    }
    @Override
    public Event getEntityById(Long eventId) {
        return repo.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }
    @Override
    public EventFullDto getById(Long eventId) {
        Event event = getEntityById(eventId);
        return eventMapper.toDto(event);
    }
    @Override
    public EventFullDto getById(Long userId, Long eventId) {
        Event event = getEntityById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("User is not the initiator of the event");
        }
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventShortDto> getByUserId(Long userId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        List<Event> events = repo.findByInitiatorId(userId,pageRequest);
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<EventFullDto> getAll(List<Long> users, List<EventState> states,
                                     List<Long> categories, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> spec = Specification.where(EventSpecifications.hasUsers(users))
                .and(EventSpecifications.hasStates(states))
                .and(EventSpecifications.hasCategories(categories))
                .and(EventSpecifications.hasRangeStart(rangeStart))
                .and(EventSpecifications.hasRangeEnd(rangeEnd));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        return repo.findAll(spec, pageRequest)
                .getContent()
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Boolean onlyAvailable, EventSort sort,
                                      int from, int size) {
        Specification<Event> spec = Specification.where(EventSpecifications.hasText(text))
                .and(EventSpecifications.hasCategories(categories))
                .and(EventSpecifications.isPaid(paid))
                .and(EventSpecifications.hasRangeStart(rangeStart))
                .and(EventSpecifications.hasRangeEnd(rangeEnd))
                .and(EventSpecifications.isOnlyAvailable(onlyAvailable));

        Sort sorting = sort == EventSort.EVENT_DATE ? Sort.by(Sort.Order.asc("eventDate")) : Sort.by(Sort.Order.desc("views"));
        PageRequest pageRequest = PageRequest.of(from / size, size, sorting);

        return repo.findAll(spec, pageRequest)
                .getContent()
                .stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }
}

