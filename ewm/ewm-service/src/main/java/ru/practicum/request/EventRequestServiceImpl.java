package ru.practicum.request;

import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;

import java.util.List;

@Service
public class EventRequestServiceImpl implements EventRequestService{
    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        return null;
    }
}
