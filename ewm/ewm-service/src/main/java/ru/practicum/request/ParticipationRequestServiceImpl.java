package ru.practicum.request;

import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService{
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return List.of();
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        return null;
    }
}
