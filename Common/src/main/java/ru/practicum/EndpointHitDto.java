package ru.practicum;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
public class EndpointHitDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;

    @IpAddress
    private String ip;
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

}