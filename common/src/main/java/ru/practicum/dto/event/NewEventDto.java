package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotNull
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    LocationDto locationDto;
    Boolean paid;
    @Positive
    Integer participantLimit = 0;
    Boolean requestModeration = true;
    @NotNull
    @Size(min = 3, max = 120)
    String title;
}
