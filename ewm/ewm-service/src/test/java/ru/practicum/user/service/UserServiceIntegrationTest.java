package ru.practicum.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private Long userId;

    @BeforeEach
    public void setUp() {
        // Создание тестового пользователя
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userId = userRepository.save(user).getId();
    }

    @Test
    public void testCreateUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New User");
        newUserDto.setEmail("newuser@example.com");

        UserDto createdUser = userService.create(newUserDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("New User");
        assertThat(createdUser.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    public void testDeleteUser() {
        userService.delete(userId);

        assertThrows(NotFoundException.class, () -> userService.getEntityById(userId));
    }

    @Test
    public void testDeleteUser_NotFound() {
        Long nonExistentUserId = 999L; // Предполагаем, что этого пользователя нет

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.delete(nonExistentUserId));
        assertThat(exception.getMessage()).isEqualTo("User with id=" + nonExistentUserId + " was not found");
    }

    @Test
    public void testGetEntityById() {
        User user = userService.getEntityById(userId);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    public void testGetEntityById_NotFound() {
        Long nonExistentUserId = 999L; // Предполагаем, что этого пользователя нет

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getEntityById(nonExistentUserId));
        assertThat(exception.getMessage()).isEqualTo("User with id=" + nonExistentUserId + " was not found");
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        List<UserDto> users = userService.getAll(Collections.emptyList(), 0, 10);

        assertThat(users).isNotNull();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getId()).isEqualTo(userId);
    }

    @Test
    public void testGetAllUsers_WithIds() {
        List<UserDto> users = userService.getAll(Arrays.asList(userId), 0, 10);

        assertThat(users).isNotNull();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getId()).isEqualTo(userId);
    }

    @Test
    public void testGetAllUsers_WithPagination() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Another User");
        newUserDto.setEmail("anotheruser@example.com");

        userService.create(newUserDto);

        List<UserDto> users = userService.getAll(Collections.emptyList(), 0, 10);

        assertThat(users).isNotNull();
        assertThat(users).hasSize(2); // Должны получить обоих пользователей
    }
}