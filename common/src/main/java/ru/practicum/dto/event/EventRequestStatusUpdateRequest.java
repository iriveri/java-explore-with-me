package ru.practicum.dto.event;

import java.util.List;

public class EventRequestStatusUpdateRequest {
    enum requestStatus{CONFIRMED, REJECTED}
    List<Integer> requestIds;
    requestStatus status;
}
