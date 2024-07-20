package ru.practicum;

import java.util.List;

public interface StatsService {
    void createRecord(EndpointHitDto newData);

    List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, boolean unique);
}
