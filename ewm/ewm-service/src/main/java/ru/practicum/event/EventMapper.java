package ru.practicum.event;

import org.mapstruct.*;
import ru.practicum.dto.event.*;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    Event fromDto(NewEventDto event);

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

