package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;


public class StatisticClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public StatisticClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public void hitStatistic(String app, String uri, String ip, LocalDateTime timestamp) {
        String url = serverUrl + "/hit";
        EndpointHitDto hitData = new EndpointHitDto(app, uri, ip, timestamp);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(hitData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
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

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Логирование или обработка неуспешного ответа
            return List.of();
        }
    }

}