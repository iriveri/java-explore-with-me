package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.practicum.category.CategoryMapper;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.EventUpdateRequest;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.event.user.UserUpdateEventRequest;
import ru.practicum.user.UserMapper;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    Event fromDto(NewEventDto newEventDto);

    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventDto toDto(Event event);

    EventShortDto toShortDto(Event event);

    default void updateEventFromAdminRequest(AdminUpdateEventRequest updateRequest, @MappingTarget Event event) {
        updateEventBasicFields(event, updateRequest);

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
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

    default void updateEventFromUserRequest(UserUpdateEventRequest updateRequest, @MappingTarget Event event) {
        updateEventBasicFields(event, updateRequest);

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }
    }

    private <T extends EventUpdateRequest> void updateEventBasicFields(@MappingTarget Event event, T updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLat(updateRequest.getLocation().getLat());
            event.setLon(updateRequest.getLocation().getLon());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
    }
}

