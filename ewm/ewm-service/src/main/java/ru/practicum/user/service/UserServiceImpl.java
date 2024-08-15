package ru.practicum.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        User newUser = userMapper.fromDto(newUserDto);
        return userMapper.toDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("User with id=" + userId + " was not found");

        userRepository.deleteById(userId);
    }

    @Override
    public User getEntityById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    @Override
    public List<UserDto> getAll(List<Long> userIds, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        Page<User> users;

        if (userIds.isEmpty()) {
            users = userRepository.findAll(pageRequest);
        } else {
            users = userRepository.findByIds(userIds, pageRequest);
        }

        return users.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
