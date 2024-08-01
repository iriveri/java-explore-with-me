package ru.practicum.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.requests.EventRequestStatusUpdateResult;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class UserEventRequestsController {

    private final EventRequestService eventRequestService;

    public UserEventRequestsController(EventRequestService eventRequestService) {
        this.eventRequestService = eventRequestService;
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список.
     *
     * @param userId id текущего пользователя
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий список {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.debug("Endpoint GET /users/{}/events/{}/requests has been reached", userId, eventId);
        List<ParticipationRequestDto> requests = eventRequestService.getEventParticipants(userId, eventId);
        log.info("Participation requests for user {} and event {} fetched successfully, total requests: {}", userId, eventId, requests.size());
        return ResponseEntity.ok(requests);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя.
     * Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется.
     * Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409).
     * Статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409).
     * Если при подтверждении данной заявки лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить.
     *
     * @param userId id текущего пользователя
     * @param eventId id события
     * @param updateRequest {@link EventRequestStatusUpdateRequest} Новый статус для заявок на участие в событии текущего пользователя
     * @return {@link ResponseEntity} содержащий обьект {@link EventRequestStatusUpdateResult} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {

        log.debug("Endpoint PATCH /users/{}/events/{}/requests has been reached with UpdateRequest: {}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = eventRequestService.changeRequestStatus(userId, eventId, updateRequest);
        log.info("Request status for user {} and event {} updated successfully", userId, eventId);
        return ResponseEntity.ok(result);
    }
}
