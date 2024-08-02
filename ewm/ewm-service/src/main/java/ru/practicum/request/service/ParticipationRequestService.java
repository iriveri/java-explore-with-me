package ru.practicum.request.service;

import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.request.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    ParticipationRequestDto delete(Long userId, Long requestId);

    ParticipationRequest getEntityById(Long requestId);

    List<ParticipationRequestDto> getByUserId(Long userId);

    List<ParticipationRequestDto> getByEventId(Long userId, Long eventId);


}
