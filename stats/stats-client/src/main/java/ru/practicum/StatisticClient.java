package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.dto.statistics.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatisticClient {

    private final WebClient webClient;


    public StatisticClient(@Autowired WebClient webClient) {
        this.webClient = webClient;
    }

    public void hitStatistic(String app, String uri, String ip, LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String hitData = "{ \"app\": \"" + app + "\", \"uri\": \"" + uri + "\", \"ip\": \"" + ip + "\", \"timestamp\": \"" + timestamp.format(formatter) + "\" }";

        String url = "/hit";


        webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(hitData)
                .retrieve()
                .onStatus(HttpStatus::is2xxSuccessful, clientResponse -> {
                    // Логирование или обработка успешного ответа
                    return Mono.empty(); // Успешный ответ
                })
                .onStatus(HttpStatus::isError, clientResponse -> {
                    // Логирование или обработка неуспешного ответа
                    return Mono.error(new RuntimeException("Ошибка при отправке данных"));
                })
                .bodyToMono(String.class)
                .block();
    }

    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String url = "/stats?start=" + start.format(formatter) + "&end=" + end.format(formatter) + "&unique=" + unique;


        if (uris != null && !uris.isEmpty()) {
            url = url + "&uris=" + String.join(",", uris);

        }

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .doOnError(error -> log.error("Error retrieving statistics for start: {}, end: {}", start, end))
                .doOnSuccess(response -> log.info("Statistics response: {}", response))
                .block(); // Блокируем для получения результата
    }
}