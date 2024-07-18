package ru.practicum;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StatsService {
    void createRecord(ViewStatsDto newData);

    List<EndpointHitDto> getStatistics(String start, String end, Optional<List<String>> uris, boolean unique);
}
