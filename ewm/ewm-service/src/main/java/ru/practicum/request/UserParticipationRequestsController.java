package ru.practicum.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class UserParticipationRequestsController {

    private final ParticipationRequestService participationRequestService;

    public UserParticipationRequestsController(ParticipationRequestService participationRequestService) {
        this.participationRequestService = participationRequestService;
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список.
     *
     * @param userId id текущего пользователя
     * @return {@link ResponseEntity} содержащий список {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.debug("Endpoint GET /users/{}/requests has been reached", userId);
        List<ParticipationRequestDto> requests = participationRequestService.getUserRequests(userId);
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
     * @param userId id текущего пользователя
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий объект {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.debug("Endpoint POST /users/{}/requests has been reached with eventId: {}", userId, eventId);
        ParticipationRequestDto requestDto = participationRequestService.addParticipationRequest(userId, eventId);
        log.info("Participation request for user {} and event {} created successfully", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
    }

    /**
     * Отмена своего запроса на участие в событии.
     *
     * @param userId id текущего пользователя
     * @param requestId id запроса на участие
     * @return {@link ResponseEntity} содержащий объект {@link ParticipationRequestDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        log.debug("Endpoint PATCH /users/{}/requests/{}/cancel has been reached", userId, requestId);
        ParticipationRequestDto requestDto = participationRequestService.cancelRequest(userId, requestId);
        log.info("Participation request {} for user {} canceled successfully", requestId, userId);
        return ResponseEntity.ok(requestDto);
    }
}
