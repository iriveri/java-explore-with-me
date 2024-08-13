package ru.practicum.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.requests.EventRequestStatusUpdateCommand;
import ru.practicum.dto.requests.EventRequestStatusUpdateResponse;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.request.service.ParticipationRequestService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/users/{userId}")
public class ParticipationRequestsController {

    private final ParticipationRequestService participationRequestService;

    public ParticipationRequestsController(ParticipationRequestService participationRequestService) {
        this.participationRequestService = participationRequestService;
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список.
     *
     * @param userId id текущего пользователя
     * @return {@link ResponseEntity} содержащий список {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.debug("Endpoint GET /users/{}/requests has been reached", userId);
        List<ParticipationRequestDto> requests = participationRequestService.getByUserId(userId);
        log.info("Participation requests for user {} fetched successfully, total requests: {}", userId, requests.size());
        return ResponseEntity.ok(requests);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии.
     * Нельзя добавить повторный запрос (Ожидается код ошибки 409).
     * Инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409).
     * Нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409).
     * Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409).
     * Если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного.
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий объект {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping("/requests")
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.debug("Endpoint POST /users/{}/requests has been reached with eventId: {}", userId, eventId);
        ParticipationRequestDto requestDto = participationRequestService.create(userId, eventId);
        log.info("Participation request for user {} and event {} created successfully", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
    }

    /**
     * Отмена своего запроса на участие в событии.
     *
     * @param userId    id текущего пользователя
     * @param requestId id запроса на участие
     * @return {@link ResponseEntity} содержащий объект {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        log.debug("Endpoint PATCH /users/{}/requests/{}/cancel has been reached", userId, requestId);
        ParticipationRequestDto requestDto = participationRequestService.delete(userId, requestId);
        log.info("Participation request {} for user {} canceled successfully", requestId, userId);
        return ResponseEntity.ok(requestDto);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список.
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий список {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.debug("Endpoint GET /users/{}/events/{}/requests has been reached", userId, eventId);
        List<ParticipationRequestDto> requests = participationRequestService.getByEventId(userId, eventId);
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
     * @param userId        id текущего пользователя
     * @param eventId       id события
     * @param updateRequest {@link EventRequestStatusUpdateCommand} Новый статус для заявок на участие в событии текущего пользователя
     * @return {@link ResponseEntity} содержащий обьект {@link EventRequestStatusUpdateResponse} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResponse> changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody(required = false) Optional<EventRequestStatusUpdateCommand> updateRequest) {

        log.debug("Endpoint PATCH /users/{}/events/{}/requests has been reached with UpdateRequest: {}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResponse result = participationRequestService.updateStatus(userId, eventId, updateRequest.orElse(new EventRequestStatusUpdateCommand()));
        log.info("Request status for user {} and event {} updated successfully", userId, eventId);
        return ResponseEntity.ok(result);
    }
}
