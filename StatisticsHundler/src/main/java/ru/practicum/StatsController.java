package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class StatsController {
    private final StatsService service;

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public ResponseEntity<String> createRecord(@RequestBody ViewStatsDto newData) {
        log.debug("Endpoint /hit has been reached by {}", newData.toString());
        service.createRecord(newData);
        log.info("New statistics created about {}", newData.getApp());
        return ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<EndpointHitDto>> getStatistics(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) Optional<List<String>> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.debug("Endpoint /stats has been reached with start: {}, end: {}, uris: {}, unique: {}",
                start, end, uris.get(), unique);

        List<EndpointHitDto> stats = service.getStatistics(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }
}

