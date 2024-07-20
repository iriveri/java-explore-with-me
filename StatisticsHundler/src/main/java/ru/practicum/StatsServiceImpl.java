package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        ClientStatistics viewStats = new ClientStatistics();
        viewStats.setApp(newData.getApp());
        viewStats.setUri(newData.getUri());
        viewStats.setIp(newData.getIp());
        viewStats.setTimestamp(parseDate(newData.getTimestamp()));
        repository.save(viewStats);
    }

    @Override
    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, boolean unique) {
        Date startDate = parseDate(start);
        Date endDate = parseDate(end);

        List<ViewStatsDto> stats;
        if (unique) {
            stats = repository.findUniqueStatistics(startDate, endDate, uris);
        } else {
            stats = repository.findStatistics(startDate, endDate, uris);
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
