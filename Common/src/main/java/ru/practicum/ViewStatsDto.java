package ru.practicum;

import lombok.Data;

import java.net.URI;
import java.time.Instant;

@Data
public class ViewStatsDto {
    String app;
    URI uri;
    String ip;
    Instant timestamp;
}
