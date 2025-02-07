package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.user.UserUpdateEventRequest;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    public PrivateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Получение событий, добавленных текущем пользователем.
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список.
     *
     * @param userId id пользователя
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link EventShortDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<EventShortDto>> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {

        log.debug("Endpoint GET /users/{}/events has been reached with from: {}, size: {}", userId, from, size);
        List<EventShortDto> events = eventService.getByUserId(userId, from, size);
        log.info("User's {} events fetched successfully", userId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Добавление нового события.
     * Дата и время, на которые намечено событие, не могут быть раньше, чем через два часа от текущего момента.
     *
     * @param userId   id текущего пользователя
     * @param newEvent {@link NewEventDto} данные добавляемого события
     * @return {@link ResponseEntity} содержащий событие {@link EventDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<EventDto> addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEvent) {

        log.debug("Endpoint POST /users/{}/events has been reached with NewEventDto: {}", userId, newEvent);

        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RuntimeException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + newEvent.getEventDate().toString());
        }

        EventDto createdEvent = eventService.create(userId, newEvent);
        log.info("User's {} event {} created successfully", userId, createdEvent.getId());
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    /**
     * Получение полной информации о событии, добавленном текущим пользователем.
     * В случае, если событие с заданным id не найдено, возвращает статус код 404.
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий событие {@link EventDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.debug("Endpoint GET /users/{}/events/{} has been reached", userId, eventId);
        EventDto event = eventService.getById(userId, eventId);
        log.info("User's {} event {} fetched successfully", userId, event.getId());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    /**
     * Изменение события, добавленного текущим пользователем.
     * Изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409).
     * Дата и время, на которые намечено событие, не могут быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409).
     *
     * @param userId                 id текущего пользователя
     * @param eventId                id события
     * @param userUpdateEventRequest {@link UserUpdateEventRequest} данные для обновления события
     * @return {@link ResponseEntity} содержащий событие {@link EventDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UserUpdateEventRequest userUpdateEventRequest) {

        log.debug("Endpoint PATCH /users/{}/events/{} has been reached with UpdateEventUserRequest: {}", userId, eventId, userUpdateEventRequest);
        EventDto updatedEvent = eventService.update(userId, eventId, userUpdateEventRequest);
        log.info("User's {} event {} updated successfully", userId, updatedEvent.getId());
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
}
