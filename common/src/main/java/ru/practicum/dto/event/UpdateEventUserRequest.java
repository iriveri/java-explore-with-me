package ru.practicum.dto.event;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.dto.Location;
import ru.practicum.dto.StateAction;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public class UpdateEventUserRequest {

    @Max(2000)
    @Min(20)
    String annotation;
    Long category;
    @Max(7000)
    @Min(20)
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
    @Max(120)
    @Min(3)
    String title;
}
