package ru.practicum.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ConditionNotMetException;
import ru.practicum.NotFoundException;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestMapper;
import ru.practicum.request.ParticipationRequestRepo;
import ru.practicum.user.User;
import ru.practicum.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final EventService eventService;
    private final UserService userService;
    private final ParticipationRequestRepo repo;
    private final ParticipationRequestMapper mapper;

    @Autowired
    public ParticipationRequestServiceImpl(EventService eventService, UserService userService, ParticipationRequestRepo repo, ParticipationRequestMapper mapper) {
        this.eventService = eventService;
        this.userService = userService;
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userService.getEntityById(userId);
        Event event = eventService.getEntityById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionNotMetException("Event is not published.");
        }

        // Проверка, что инициатор события не может добавить запрос
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Cannot participate in your own event.");
        }

        // Проверка, что запрос от пользователя уже существует
        if (repo.existsByParticipantIdAndEventId(userId, eventId)) {
            throw new ConditionNotMetException("Participation request already exists.");
        }

        // Проверка на лимит запросов на участие
        if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0 &&
                repo.countByEventId(eventId) >= event.getParticipantLimit()) {
            throw new ConditionNotMetException("Participant limit reached.");
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setParticipant(user);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());

        // Если пре-модерация отключена, запрос автоматически подтвержден
        if (!event.getRequestModeration()) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(RequestStatus.PENDING);
        }

        return mapper.toDto(repo.save(participationRequest));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventService.getEntityById(eventId);

        // Проверка, что пользователь является организатором события
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Only event initiator can update request status.");
        }

        Integer limit = event.getParticipantLimit();
        List<ParticipationRequest> requestsToUpdate = repo.findAllById(updateRequest.getRequestIds());
        boolean noLimitOrModeration = limit == null || limit == 0 || !event.getRequestModeration();

        for (ParticipationRequest request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConditionNotMetException("Only requests in pending status can be updated.");
            }
            if (noLimitOrModeration || (updateRequest.getStatus().equals(RequestStatus.CONFIRMED) && repo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) < limit)) {
                request.setStatus(updateRequest.getStatus());
            } else {
                request.setStatus(RequestStatus.REJECTED);
            }
        }

        repo.saveAll(requestsToUpdate);

        List<ParticipationRequestDto> confirmedRequests = requestsToUpdate.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .map(mapper::toDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requestsToUpdate.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.REJECTED))
                .map(mapper::toDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        return result;
    }

    @Override
    public ParticipationRequestDto delete(Long userId, Long requestId) {
        // Проверка, что запрос существует и принадлежит пользователю
        ParticipationRequest request = getEntityById(requestId);
        if (!request.getParticipant().getId().equals(userId)) {
            throw new ConditionNotMetException("Request does not belong to user.");
        }
        request.setStatus(RequestStatus.CANCELED);
        repo.delete(request);
        return mapper.toDto(request);
    }

    @Override
    public ParticipationRequest getEntityById(Long requestId) {
        return repo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found."));
    }

    @Override
    public List<ParticipationRequestDto> getByUserId(Long userId) {
        return repo.findByParticipantId(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long userId, Long eventId) {
        Event event = eventService.getEntityById(eventId);
        // Проверка, что пользователь является организатором события
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("User is not the event initiator.");
        }

        return repo.findByEventId(eventId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}


