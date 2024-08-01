package ru.practicum.user.service;

import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUser);

    List<UserDto> getUsers(List<Long> ids, int offset, int limit);

    void delete(long userId);

}
