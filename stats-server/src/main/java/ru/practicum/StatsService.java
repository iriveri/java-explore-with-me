package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void createRecord(EndpointHitDto newData);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}