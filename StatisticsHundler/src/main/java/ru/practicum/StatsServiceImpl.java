package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService{
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
            stats = repository.findUniqueStatistics(start, end, uris);
        } else {
            stats = repository.findStatistics(start, end, uris);
        }

        return stats;
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }
}
