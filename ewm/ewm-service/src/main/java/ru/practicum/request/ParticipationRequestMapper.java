package ru.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.requests.ParticipationRequestDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface ParticipationRequestMapper {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "participant.id", target = "requester")
    ParticipationRequestDto toDto(ParticipationRequest request);
}
