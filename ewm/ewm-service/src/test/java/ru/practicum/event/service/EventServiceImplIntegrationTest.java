package ru.practicum.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepo;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional // Откат транзакций после каждого теста
@AutoConfigureMockMvc
public class EventServiceImplIntegrationTest {

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventMapper eventMapper;

    private Long userId;
    private Long categoryId;
    private NewEventDto newEventDto;

    @BeforeEach
    public void setUp() {
// Инициализация пользователя и категории для тестов

        userId = userService.create(new NewUserDto("just.bob@bobs.ru", "MR.BOBS")).getId();
        categoryId = categoryService.create(new NewCategoryDto("Pizza")).getId();

        newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setCategory(categoryId);
        newEventDto.setEventDate(LocalDateTime.now().plusHours(3)); // Устанавливаем дату события через 3 часа
        // Установите другие поля, если они есть

        // Создаем тестовые события
        eventService.create(userId, newEventDto);
        newEventDto.setTitle("Test Event 2");
        eventService.create(userId, newEventDto);
    }

    @Test
    public void testCreateEvent() {
        EventFullDto createdEvent = eventService.create(userId, newEventDto);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo(newEventDto.getTitle());
        assertThat(createdEvent.getInitiator().getId()).isEqualTo(userId);

        // Проверяем, что событие сохранено в базе данных
        Optional<Event> foundEvent = eventRepo.findById(createdEvent.getId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getTitle()).isEqualTo(newEventDto.getTitle());
    }

    @Test
    public void testUpdateEvent() {
        EventFullDto createdEvent = eventService.create(userId, newEventDto);

        UpdateEventAdminDto updateEventDto = new UpdateEventAdminDto();
        updateEventDto.setStateAction(AdminStateAction.PUBLISH_EVENT);
        updateEventDto.setEventDate(LocalDateTime.now().plusHours(4)); // Обновляем дату события

        EventFullDto updatedEvent = eventService.update(createdEvent.getId(), updateEventDto);

        assertThat(updatedEvent.getState()).isEqualTo(EventState.PUBLISHED);

        // Проверяем, что обновление прошло успешно в базе данных
        Event updatedEntity = eventRepo.findById(createdEvent.getId()).orElseThrow();
        assertThat(updatedEntity.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    public void testUpdateUserEvent() {
        EventFullDto createdEvent = eventService.create(userId, newEventDto);

        UpdateEventUserDto updateUserDto = new UpdateEventUserDto();
        updateUserDto.setEventDate(LocalDateTime.now().plusHours(5)); // Обновляем дату события

        EventFullDto updatedUserEvent = eventService.update(userId, createdEvent.getId(), updateUserDto);

        assertThat(updatedUserEvent.getEventDate()).isEqualTo(updateUserDto.getEventDate());

        // Проверяем, что обновление прошло успешно
        // в базе данных
        Event updatedEntity = eventRepo.findById(createdEvent.getId()).orElseThrow();
        assertThat(updatedEntity.getEventDate()).isEqualTo(updateUserDto.getEventDate());
    }

    @Test
    public void testGetById() {
        EventFullDto createdEvent = eventService.create(userId, newEventDto);
        UpdateEventAdminDto dto = new UpdateEventAdminDto();
        dto.setStateAction(AdminStateAction.PUBLISH_EVENT);
        eventService.update(createdEvent.getId(), dto);
        EventFullDto foundEvent = eventService.getById(createdEvent.getId());

        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getId()).isEqualTo(createdEvent.getId());
        assertThat(foundEvent.getTitle()).isEqualTo(newEventDto.getTitle());
    }

    @Test
    public void testGetByIdAsUser() {
        EventFullDto createdEvent = eventService.create(userId, newEventDto);

        EventFullDto foundEvent = eventService.getById(userId, createdEvent.getId());

        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getInitiator().getId()).isEqualTo(userId);
    }

    @Test
    public void testGetAllWithFilters() {
        List<EventFullDto> events = eventService.getAll(
                Collections.singletonList(userId),
                List.of(EventState.PENDING),
                Collections.singletonList(categoryId),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                0,
                10
        );

        assertThat(events).isNotEmpty();
        assertThat(events.size()).isGreaterThan(0); // Ожидаем, что хотя бы одно событие будет возвращено
    }

    @Test
    public void testGetAllWithSearch() {
        List<EventShortDto> events = eventService.getAll(
                "Test",
                Collections.singletonList(categoryId),
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                null,
                EventSort.EVENT_DATE,
                0,
                10
        );

        assertThat(events).isNotEmpty();
        assertThat(events.size()).isGreaterThan(0); // Ожидаем, что хотя бы одно событие будет возвращено
    }
}