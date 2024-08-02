package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticsService;
import ru.practicum.dto.event.EventSort;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatisticsService statisticsService;

    public PublicEventController(EventService eventService, StatisticsService statisticsService) {
        this.eventService = eventService;
        this.statisticsService = statisticsService;
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
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        log.debug("Endpoint GET /events has been reached with " +
                        "text: {}, categories: {}, paid: {}, rangeStart: {}," +
                        " rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortDto> events = eventService.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statisticsService.recordRequest("/events", events.size());
        log.info("Event's list fetched successfully with {} events", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору.
     * Событие должно быть опубликовано.
     * Информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов.
     * Информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики.
     *
     * @param id id события
     * @return {@link ResponseEntity} содержащий объект {@link EventFullDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id) {
        log.info("Endpoint GET /events/{} has been reached", id);
        EventFullDto event = eventService.getById(id);
        statisticsService.recordRequest("/events/" + id, 1);
        log.info("Event {} fetched successfully", event.getId());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
