package ru.practicum.event.location;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.event.LocationDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
  Location  fromDto( LocationDto location);
  LocationDto toDto(Location location);
}
