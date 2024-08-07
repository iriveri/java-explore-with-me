package ru.practicum.event;

import org.mapstruct.*;
import ru.practicum.category.CategoryMapper;
import ru.practicum.dto.event.*;
import ru.practicum.user.UserMapper;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    Event fromDto(NewEventDto event);

    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventFullDto toDto(Event event);

    EventShortDto toShortDto(Event event);

    default void updateEventFromAdminDto(UpdateEventAdminDto updateEvent, @MappingTarget Event event) {
        if (updateEvent.getAnnotation() != null)
            event.setAnnotation(updateEvent.getAnnotation());

        if (updateEvent.getDescription() != null)
            event.setDescription(updateEvent.getDescription());

        if (updateEvent.getEventDate() != null)
            event.setEventDate(updateEvent.getEventDate());

        if (updateEvent.getLocation() != null) {
            var locationDto = updateEvent.getLocation();
            event.setLat(locationDto.getLat());
            event.setLon(locationDto.getLon());
        }

        if (updateEvent.getPaid() != null)
            event.setPaid(updateEvent.getPaid());

        if (updateEvent.getParticipantLimit() != null)
            event.setParticipantLimit(updateEvent.getParticipantLimit());

        if (updateEvent.getRequestModeration() != null)
            event.setRequestModeration(updateEvent.getRequestModeration());

        if (updateEvent.getTitle() != null)
            event.setTitle(updateEvent.getTitle());

        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }


    }

    default void updateEventFromUserDto(UpdateEventUserDto updateEvent, @MappingTarget Event event) {
        if (updateEvent.getAnnotation() != null)
            event.setAnnotation(updateEvent.getAnnotation());

        if (updateEvent.getDescription() != null)
            event.setDescription(updateEvent.getDescription());

        if (updateEvent.getEventDate() != null)
            event.setEventDate(updateEvent.getEventDate());

        if (updateEvent.getLocation() != null) {
            var locationDto = updateEvent.getLocation();
            event.setLat(locationDto.getLat());
            event.setLon(locationDto.getLon());
        }

        if (updateEvent.getPaid() != null)
            event.setPaid(updateEvent.getPaid());

        if (updateEvent.getParticipantLimit() != null)
            event.setParticipantLimit(updateEvent.getParticipantLimit());

        if (updateEvent.getRequestModeration() != null)
            event.setRequestModeration(updateEvent.getRequestModeration());

        if (updateEvent.getTitle() != null)
            event.setTitle(updateEvent.getTitle());


        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }

    }
}

