package ru.practicum.user.service;

import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.user.User;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    void delete(Long userId);

    User getEntityById(Long userId);

    List<UserDto> getAll(List<Long> ids, int offset, int limit);

}
