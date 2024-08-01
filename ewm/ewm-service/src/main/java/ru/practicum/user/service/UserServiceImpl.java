package ru.practicum.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepo;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo repo;
    private final UserMapper mapper;
    @Autowired
    public UserServiceImpl(UserRepo userRepository, UserMapper mapper) {
        this.repo = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDto create(NewUserDto newUser) {
        User savedUser = repo.save( mapper.fromDto(newUser));
        return mapper.toDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int offset, int limit){
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return  repo.getAllUsers(ids,pageRequest).getContent();
    }

    @Override
    public void delete(long userId) {
        repo.deleteById(userId);
    }
}
