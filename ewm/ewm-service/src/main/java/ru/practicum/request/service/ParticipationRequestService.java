package ru.practicum.request.service;

import ru.practicum.dto.requests.EventRequestStatusUpdateCommand;
import ru.practicum.dto.requests.EventRequestStatusUpdateResponse;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.request.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResponse updateStatus(Long userId, Long eventId, EventRequestStatusUpdateCommand updateRequest);

    ParticipationRequestDto delete(Long userId, Long requestId);

    ParticipationRequest getEntityById(Long requestId);

    List<ParticipationRequestDto> getByUserId(Long userId);

    List<ParticipationRequestDto> getByEventId(Long userId, Long eventId);

}
