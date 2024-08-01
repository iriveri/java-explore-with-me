package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

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
     * @param ids  id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link UserDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size) {

        log.debug("Endpoint GET /admin/users has been reached with ids: {}, from: {}, size: {}", ids, from, size);
        List<UserDto> users = userService.getUsers(ids, from, size);
        log.info("Users fetched successfully");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUser {@link NewUserRequest} данные добавляемого пользователя
     * @return {@link ResponseEntity} содержащий объект {@link UserDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserRequest newUser) {
        log.debug("Endpoint POST /admin/users has been reached with NewUserRequest: {}", newUser);
        UserDto createdUser = userService.addUser(newUser);
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
        userService.deleteUser(userId);
        log.info("User {} deleted successfully", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
