package ru.practicum.request;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    public List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
