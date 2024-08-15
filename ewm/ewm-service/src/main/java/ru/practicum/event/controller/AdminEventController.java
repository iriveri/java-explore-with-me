package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Поиск событий.
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия.
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список.
     *
     * @param users      список id пользователей, чьи события нужно найти
     * @param states     список состояний, в которых находятся искомые события
     * @param categories список id категорий, в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd   дата и время не позже которых должно произойти событие
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       количество событий в наборе
     * @return {@link ResponseEntity} содержащий список {@link EventDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<EventDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Invalid input: 'end' date is before 'start' date");
        }
        log.debug("Endpoint GET /admin/events has been reached with users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventDto> events = eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Events list for admin role fetched successfully with {} events", events.size());

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * Событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * Событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     *
     * @param eventId                 id события
     * @param adminUpdateEventRequest {@link AdminUpdateEventRequest} данные для изменения информации о событии
     * @return {@link ResponseEntity} содержащий событие {@link EventDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {

        log.debug("Endpoint PATCH /admin/events/{} has been reached with UpdateEventAdminRequest: {}", eventId, adminUpdateEventRequest);

        EventDto updatedEvent = eventService.update(eventId, adminUpdateEventRequest);
        log.info("Event {} status was changed successfully", eventId);

        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
}
