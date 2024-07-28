package ru.practicum.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class UserEventRequestsController {

    private final EventRequestService eventRequestService;

    public UserEventRequestsController(EventRequestService eventRequestService) {
        this.eventRequestService = eventRequestService;
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        List<ParticipationRequestDto> requests = eventRequestService.getEventParticipants(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = eventRequestService.changeRequestStatus(userId, eventId, updateRequest);
        return ResponseEntity.ok(result);
    }
}