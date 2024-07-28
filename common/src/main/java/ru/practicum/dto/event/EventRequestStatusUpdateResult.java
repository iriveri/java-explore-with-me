package ru.practicum.dto.event;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
