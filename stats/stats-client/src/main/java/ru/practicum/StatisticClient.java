package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.statistics.EndpointHitDto;
import ru.practicum.dto.statistics.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;


    @Autowired
    public StatisticClient() {
        this.restTemplate = new RestTemplate();
        this.serverUrl = "./stats/stats-server";
    }

    public void hitStatistic(String app, String uri, String ip, LocalDateTime timestamp) {
        String url = serverUrl + "/hit";
        EndpointHitDto hitData = new EndpointHitDto(app, uri, ip, timestamp);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(hitData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            // Логирование или обработка успешного ответа
        } else {
            // Логирование или обработка неуспешного ответа
        }
    }

    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String url = serverUrl + "/stats";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Добавляем параметры запроса
        HttpHeaders params = new HttpHeaders();
        params.set("start", start.toString());
        params.set("end", end.toString());
        params.set("unique", Boolean.toString(unique));
        if (uris != null && !uris.isEmpty()) {
            params.set("uris", String.join(",", uris));
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {
                }, params);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        } else {
            // Логирование или обработка неуспешного ответа
            return List.of();
        }
    }

}