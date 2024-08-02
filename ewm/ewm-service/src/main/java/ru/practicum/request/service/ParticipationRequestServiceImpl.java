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
        if (event.getParticipantLimit() != null &&
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
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventService.getEntityById(eventId);
        // Проверка, что пользователь является организатором события
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Only event initiator can update request status.");
        }

        // Проверка, что количество подтвержденных запросов не превышает лимит
        long confirmedRequests = repo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != null &&
                confirmedRequests >= event.getParticipantLimit() &&
                updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConditionNotMetException("Participant limit reached. Cannot confirm more requests.");
        }

        // Обновление статусов запросов
        List<ParticipationRequest> requestsToUpdate = repo.findAllById(updateRequest.getRequestIds());

        for (ParticipationRequest request : requestsToUpdate) {
            // Проверка, что заявка находится в состоянии ожидания
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConditionNotMetException("Only requests in pending status can be updated.");
            }

            request.setStatus(updateRequest.getStatus());
        }

        repo.saveAll(requestsToUpdate);

        // Если при подтверждении данной заявки лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить.
        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED) && event.getParticipantLimit() != null) {
            confirmedRequests = repo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedRequests >= event.getParticipantLimit()) {
                List<ParticipationRequest> pendingRequests = repo.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (ParticipationRequest pendingRequest : pendingRequests) {
                    pendingRequest.setStatus(RequestStatus.REJECTED);
                }
                repo.saveAll(pendingRequests);
            }
        }

        // Формирование результата
        List<ParticipationRequestDto> updatedRequests = requestsToUpdate.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setUpdatedRequests(updatedRequests);
        return result;
    }

    @Override
    public ParticipationRequestDto delete(Long userId, Long requestId) {
        // Проверка, что запрос существует и принадлежит пользователю
        ParticipationRequest request = getEntityById(requestId);
        if (!request.getParticipant().getId().equals(userId)) {
            throw new ConditionNotMetException("Request does not belong to user.");
        }

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


