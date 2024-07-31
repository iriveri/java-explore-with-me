package ru.practicum.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Поиск событий.
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     *
     * @param users список id пользователей, чьи события нужно найти
     * @param states список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd дата и время не позже которых должно произойти событие
     * @param from количество событий, которые нужно пропустить для формирования текущего набора
     * @param size количество событий в наборе
     * @return {@link ResponseEntity} содержащий список {@link EventFullDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) Optional<List<Long>> users,
            @RequestParam(required = false) Optional<List<String>> states,
            @RequestParam(required = false) Optional<List<Long>> categories,
            @RequestParam(required = false) Optional<String> rangeStart,
            @RequestParam(required = false) Optional<String> rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size){
        log.debug("Endpoint GET /admin/events has been reached with users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {},",
                users.orElse(List.of(null)),
                states.orElse(List.of("Empty")),
                categories.orElse(List.of(null)),
                rangeStart.orElse("Empty"),
                rangeEnd.orElse("Empty"),
                from, size);
        List<EventFullDto> events = eventService.getEvents(
                users.orElse(List.of()),
                states.orElse(List.of()),
                categories.orElse(List.of()),
                rangeStart.orElse(""),
                rangeEnd.orElse(""),
                from, size);
        log.info("Event`s list for admin role fetched successfully");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * Событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * Событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     *
     * @param eventId id события
     * @param updateEventAdminRequest {@link UpdateEventAdminRequest} Данные для изменения информации о событиия
     * @return {@link ResponseEntity} содержащий событие {@link EventFullDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Endpoint PATCH /admin/events/{} has been reached with UpdateEventAdminRequest: {}",
                eventId,updateEventAdminRequest);

        EventFullDto updatedEvent = eventService.updateEvent(eventId, updateEventAdminRequest);
        log.info("Event`s {} status was changed successfully",eventId);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
}