package ru.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение информации о пользователях.
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), либо о конкретных (учитываются указанные идентификаторы).
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список.
     *
     * @param userIds id пользователей
     * @param from    количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size    количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link UserDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(value = "ids", required = false) Optional<List<Long>> userIds,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {

        log.debug("Endpoint GET /admin/users has been reached with ids: {}, from: {}, size: {}", userIds, from, size);
        List<UserDto> users = userService.getAll(userIds.orElse(Collections.emptyList()), from, size);
        log.info("Users fetched successfully");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUser {@link NewUserDto} данные добавляемого пользователя
     * @return {@link ResponseEntity} содержащий объект {@link UserDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserDto newUser) {
        log.debug("Endpoint POST /admin/users has been reached with NewUserRequest: {}", newUser);
        UserDto createdUser = userService.create(newUser);
        log.info("User {} created successfully", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Удаление пользователя.
     *
     * @param userId id пользователя
     * @return {@link ResponseEntity} содержащий статус ответа {@link HttpStatus#NO_CONTENT}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        log.debug("Endpoint DELETE /admin/users/{} has been reached", userId);
        userService.delete(userId);
        log.info("User {} deleted successfully", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
