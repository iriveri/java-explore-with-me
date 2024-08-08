package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticClient;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSortOption;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.admin.AdminAction;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.event.user.UserUpdateEventRequest;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventSpecifications;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final StatisticClient statisticClient;
    private final ParticipationRequestRepository participationRequestRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserService userService,
                            CategoryService categoryService, EventMapper eventMapper, StatisticClient statisticClient, ParticipationRequestRepository participationRequestRepository) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.eventMapper = eventMapper;
        this.statisticClient = statisticClient;
        this.participationRequestRepository = participationRequestRepository;
    }

    @Override
    @Transactional
    public EventDto create(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.fromDto(newEventDto);
        event.setInitiator(userService.getEntityById(userId));
        event.setCategory(categoryService.getEntityById(newEventDto.getCategory()));

        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(null);
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDto update(Long eventId, AdminUpdateEventRequest updateEvent) {
        Event event = getEntityById(eventId);

        if (updateEvent.getEventDate() != null &&
                event.getPublishedOn() != null &&
                updateEvent.getEventDate().isBefore(event.getPublishedOn().plusHours(1)))
            throw new ConditionNotMetException("Event date must be at least one hour after the publication date.");

        if (AdminAction.PUBLISH_EVENT.equals(updateEvent.getStateAction()) &&
                !EventState.PENDING.equals(event.getState()))
            throw new ConditionNotMetException("Event can only be published if it is in pending state.");

        if (AdminAction.REJECT_EVENT.equals(updateEvent.getStateAction()) &&
                EventState.PUBLISHED.equals(event.getState()))
            throw new ConditionNotMetException("Event can only be rejected if it is not published.");

        if (updateEvent.getCategory() != null)
            event.setCategory(categoryService.getEntityById(updateEvent.getCategory()));

        eventMapper.updateEventFromAdminRequest(updateEvent, event);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDto update(Long userId, Long eventId, UserUpdateEventRequest updateEvent) {
        Event event = getEntityById(eventId);

        if (!event.getInitiator().getId().equals(userId))
            throw new ConditionNotMetException("User is not the initiator of the event");

        EventState eventState = event.getState();
        if (!EventState.CANCELED.equals(eventState) && !EventState.PENDING.equals(eventState))
            throw new ConditionNotMetException("Only pending or canceled events can be changed.");

        if (updateEvent.getEventDate() != null &&
                updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConditionNotMetException("Event date must be at least two hours from the current time.");

        if (updateEvent.getCategory() != null)
            event.setCategory(categoryService.getEntityById(updateEvent.getCategory()));

        eventMapper.updateEventFromUserRequest(updateEvent, event);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public Event getEntityById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public EventDto getById(Long eventId) {
        Event event = getEntityById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event not yet published");
        }
        var dto = eventMapper.toDto(event);
        var stats = statisticClient.getStatistics(dto.getPublishedOn(), LocalDateTime.now(), List.of("/events/" + eventId), true);
        if (!stats.isEmpty()) {
            dto.setViews(stats.get(0).getHits());
        } else {
            dto.setViews(0L);
        }
        dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), RequestStatus.CONFIRMED));
        return dto;
    }

    @Override
    public EventDto getById(Long userId, Long eventId) {
        Event event = getEntityById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("User is not the initiator of the event");
        }
        var dto = eventMapper.toDto(event);
        dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), RequestStatus.CONFIRMED));
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventShortDto> getByUserId(Long userId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<EventDto> getAll(List<Long> users, List<EventState> states,
                                 List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> spec = Specification.where(EventSpecifications.hasUsers(users))
                .and(EventSpecifications.hasStates(states))
                .and(EventSpecifications.hasCategories(categories))
                .and(EventSpecifications.hasRangeStart(rangeStart))
                .and(EventSpecifications.hasRangeEnd(rangeEnd));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAll(spec, pageRequest)
                .getContent()
                .stream()
                .map(eventMapper::toDto)
                .peek(dto -> dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), RequestStatus.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<EventShortDto> getAll(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Boolean onlyAvailable, EventSortOption sort,
                                      int from, int size) {
        Specification<Event> spec = Specification.where(EventSpecifications.hasText(text))
                .and(EventSpecifications.hasCategories(categories))
                .and(EventSpecifications.isPaid(paid))
                .and(EventSpecifications.hasRangeStart(rangeStart))
                .and(EventSpecifications.hasRangeEnd(rangeEnd))
                .and(EventSpecifications.isOnlyAvailable(onlyAvailable));

        Sort sorting = sort == EventSortOption.EVENT_DATE ? Sort.by(Sort.Order.asc("eventDate")) : Sort.by(Sort.Order.desc("views"));
        PageRequest pageRequest = PageRequest.of(from / size, size, sorting);

        return eventRepository.findAll(spec, pageRequest)
                .getContent()
                .stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }
}

