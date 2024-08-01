package ru.practicum.user;

import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Override
    public UserDto addUser(NewUserRequest newUser) {
        return null;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        return null;
    }

    @Override
    public void deleteUser(long userId) {

    }
}
