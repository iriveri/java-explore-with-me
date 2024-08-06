package ru.practicum.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.NotFoundException;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public UserDto create(NewUserDto newUser) {
        User user = mapper.fromDto(newUser);
        return mapper.toDto(repo.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!repo.existsById(userId))
            throw new NotFoundException("User with id=" + userId + " was not found");

        repo.deleteById(userId);
    }

    @Override
    public User getEntityById(Long userId) {
        return repo.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        Page<User> users;

        if (ids.isEmpty()) {
            users = repo.findAll(pageRequest);
        } else {
            users = repo.findByIds(ids, pageRequest);
        }

        return users.getContent().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
