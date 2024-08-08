package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final ViewStatsRepository repository;

    @Autowired
    public StatsServiceImpl(ViewStatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createRecord(EndpointHitDto newData) {
        ClientStatistics newStatistics = new ClientStatistics();
        newStatistics.setApp(newData.getApp());
        newStatistics.setUri(newData.getUri());
        newStatistics.setIp(newData.getIp());
        newStatistics.setTimestamp(newData.getTimestamp());
        repository.save(newStatistics);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        List<ViewStatsDto> stats;
        if (unique) {

            if (uris.isEmpty())
                stats = repository.findAllUniqueStatistics(start, end);
            else
                stats = repository.findUniqueStatistics(start, end, uris);
        } else {

            if (uris.isEmpty())
                stats = repository.findAllStatistics(start, end);
            else
                stats = repository.findStatistics(start, end, uris);
        }

        return stats;
    }
}
