package ru.practicum.dto.event;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.dto.Location;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

public class NewEventDto {
    String annotation;
    CategoryDto category;
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String title;
}
