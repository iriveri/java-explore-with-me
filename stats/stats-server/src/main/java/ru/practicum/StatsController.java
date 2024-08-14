package ru.practicum;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@Slf4j
public class StatsController {
    private final StatsService service;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    /**
     * Сохранение информации о том, что к эндпоинту был запрос
     * Endpoint: POST /hit
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     * Название сервиса, uri и ip пользователя указаны в теле запроса.
     *
     * @param newData объект {@link EndpointHitDto} содержащй данные запроса
     * @return {@link ResponseEntity} содержащий "Информация сохранена" и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping("/hit")
    public ResponseEntity<String> createRecord(@RequestBody @Valid EndpointHitDto newData) {
        log.debug("Endpoint POST /hit has been reached by {}", newData.toString());
        service.createRecord(newData);
        log.info("New statistics created about {}", newData.getApp());
        return ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");
    }

    /**
     * Получение статистики по посещениям.
     * Endpoint: GET /hit
     * Получение колличественной статистики каждого пользователя по посещениям.
     * Название сервиса, uri и колличесво обращений возвращаются в теле запроса.
     * [значение даты и времени кодируется]
     *
     * @param start  Дата и время начала диапазона за который нужно выгрузить статистику
     *               (в формате \"yyyy-MM-dd HH:mm:ss\")
     * @param end    Дата и время конца диапазона за который нужно выгрузить статистику
     *               (в формате \"yyyy-MM-dd HH:mm:ss\")
     * @param uris   Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения
     *               (только с уникальным ip)
     * @return {@link ResponseEntity} содержащий список добавленных объектов {@link ViewStatsDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStatistics(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) Optional<List<String>> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = LocalDateTime.parse(start, formatter);
            endDateTime = LocalDateTime.parse(end, formatter);
        } catch (Exception e) {
            throw new ValidationException("Invalid input start or end format");
        }

        if (endDateTime.isBefore(startDateTime)) {
            throw new ValidationException("Invalid input: 'end' date is before 'start' date");
        }

        log.debug("Endpoint GET /stats has been reached with start: {}, end: {}, uris: {}, unique: {}",
                start, end, uris.orElse(List.of("Empty")), unique);

        List<ViewStatsDto> stats = service.getStatistics(startDateTime, endDateTime, uris.orElse(List.of()), unique);
        log.info("Statistics about {} uris fetched successfully", uris.orElse(List.of("all")));
        return ResponseEntity.ok(stats);
    }
}

