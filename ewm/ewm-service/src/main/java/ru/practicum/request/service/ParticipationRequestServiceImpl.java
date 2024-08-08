package ru.practicum.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.requests.EventRequestStatusUpdateCommand;
import ru.practicum.dto.requests.EventRequestStatusUpdateResponse;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestMapper;
import ru.practicum.request.ParticipationRequestRepository;
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
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Autowired
    public ParticipationRequestServiceImpl(EventService eventService, UserService userService, ParticipationRequestRepository participationRequestRepository, ParticipationRequestMapper participationRequestMapper) {
        this.eventService = eventService;
        this.userService = userService;
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userService.getEntityById(userId);
        Event event = eventService.getEntityById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionNotMetException("Event is not published.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Cannot participate in your own event.");
        }

        if (participationRequestRepository.existsByParticipantIdAndEventId(userId, eventId)) {
            throw new ConditionNotMetException("Participation request already exists.");
        }

        if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0 &&
                participationRequestRepository.countByEventId(eventId) >= event.getParticipantLimit()) {
            throw new ConditionNotMetException("Participant limit reached.");
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setParticipant(user);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(RequestStatus.PENDING);
        }

        return participationRequestMapper.toDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResponse updateStatus(Long userId, Long eventId, EventRequestStatusUpdateCommand updateRequest) {
        Event event = eventService.getEntityById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Only event initiator can update request status.");
        }

        Integer limit = event.getParticipantLimit();
        List<ParticipationRequest> requestsToUpdate = participationRequestRepository.findAllById(updateRequest.getRequestIds());
        boolean noLimitOrModeration = limit == null || limit == 0 || !event.getRequestModeration();

        for (ParticipationRequest request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConditionNotMetException("Only requests in pending status can be updated.");
            }
            if (noLimitOrModeration) {
                request.setStatus(RequestStatus.CONFIRMED);
            } else if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED) && participationRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) < limit) {
                request.setStatus(updateRequest.getStatus());
            } else {
                request.setStatus(RequestStatus.REJECTED);
            }
        }

        participationRequestRepository.saveAll(requestsToUpdate);

        List<ParticipationRequestDto> confirmedRequests = requestsToUpdate.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requestsToUpdate.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.REJECTED))
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResponse result = new EventRequestStatusUpdateResponse();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        return result;
    }

    @Override
    public ParticipationRequestDto delete(Long userId, Long requestId) {
        ParticipationRequest request = getEntityById(requestId);

        if (!request.getParticipant().getId().equals(userId)) {
            throw new ConditionNotMetException("Request does not belong to user.");
        }
        request.setStatus(RequestStatus.CANCELED);
        participationRequestRepository.delete(request);
        return participationRequestMapper.toDto(request);
    }

    @Override
    public ParticipationRequest getEntityById(Long requestId) {
        return participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found."));
    }

    @Override
    public List<ParticipationRequestDto> getByUserId(Long userId) {
        return participationRequestRepository.findByParticipantId(userId)
                .stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long userId, Long eventId) {
        Event event = eventService.getEntityById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("User is not the event initiator.");
        }

        return participationRequestRepository.findByEventId(eventId)
                .stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}


