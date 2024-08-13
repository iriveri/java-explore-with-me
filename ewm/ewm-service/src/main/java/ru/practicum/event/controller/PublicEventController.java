package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatisticClient;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSortOption;
import ru.practicum.event.service.EventService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatisticClient statisticClient;
    private final String pattern = "yyyy-MM-dd HH:mm:ss";

    public PublicEventController(EventService eventService, StatisticClient statisticClient) {
        this.eventService = eventService;
        this.statisticClient = statisticClient;
    }

    /**
     * Получение событий с возможностью фильтрации.
     * Это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события.
     * Текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв.
     * Если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени.
     * Информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие.
     * Информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики.
     *
     * @param text          текст для поиска в содержимом аннотации и подробном описании события
     * @param categories    список id категорий, в которых будет вестись поиск
     * @param paid          поиск только платных/бесплатных событий
     * @param rangeStart    дата и время не раньше которых должно произойти событие
     * @param rangeEnd      дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort          вариант сортировки: по дате события или по количеству просмотров
     * @param from          количество событий, которые нужно пропустить для формирования текущего набора
     * @param size          количество событий в наборе
     * @return {@link ResponseEntity} содержащий список {@link EventShortDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            HttpServletRequest request,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSortOption sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Invalid input: 'end' date is before 'start' date");
        }
        log.debug("Endpoint GET /events has been reached with " +
                        "text: {}, categories: {}, paid: {}, rangeStart: {}," +
                        " rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortDto> events = eventService.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statisticClient.hitStatistic("ewm-service", "/events", request.getRemoteAddr(), LocalDateTime.now());
        log.info("Event's list fetched successfully with {} events", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору.
     * Событие должно быть опубликовано.
     * Информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов.
     * Информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики.
     *
     * @param eventId id события
     * @return {@link ResponseEntity} содержащий объект {@link EventDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(HttpServletRequest request, @PathVariable Long eventId) {
        log.info("Endpoint GET /events/{} has been reached", eventId);
        EventDto event = eventService.getById(eventId);
        statisticClient.hitStatistic("ewm-service", "/events/" + eventId, request.getRemoteAddr(), LocalDateTime.now());
        log.info("Event {} fetched successfully", event.getId());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
