package ru.practicum.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User fromDto(NewUserDto userDto);

    UserDto toDto(User user);
}