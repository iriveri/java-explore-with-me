package ru.practicum.event;

import org.mapstruct.*;
import ru.practicum.dto.event.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface  EventMapper {

    @Mapping(target = "category", ignore = true)
    Event fromDto(NewEventDto event);
    EventFullDto toDto(Event event);
    EventShortDto toShortDto(Event event);
    @Mapping(source = "category", target = "category.id")
    void updateEventFromAdminDto(UpdateEventAdminDto updateEvent, @MappingTarget Event event);
    @Mapping(source = "category", target = "category.id")
    void updateEventFromUserDto(UpdateEventUserDto updateEvent, @MappingTarget Event event);
    @AfterMapping
    default void afterToDto(UpdateEventAdminDto updateEvent, @MappingTarget Event event) {
        if(updateEvent.getStateAction() == null)
            return;

        switch (updateEvent.getStateAction()){
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                break;
        }
    }
    @AfterMapping
    default void afterToDto(UpdateEventUserDto updateEvent, @MappingTarget Event event) {
        if(updateEvent.getStateAction() == null)
            return;

        switch (updateEvent.getStateAction()){
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
        }
    }
}
