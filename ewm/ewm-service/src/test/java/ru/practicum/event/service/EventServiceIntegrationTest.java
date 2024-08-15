package ru.practicum.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticClient;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSortOption;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.admin.AdminAction;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.event.user.UserUpdateEventRequest;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventMapper eventMapper;

    @MockBean
    StatisticClient statisticClient;

    private Long userId;
    private Long categoryId;
    private NewEventDto newEventDto;

    @BeforeEach
    public void setUp() {
        userId = userService.create(new NewUserDto("just.bob@bobs.ru", "MR.BOBS")).getId();
        categoryId = categoryService.create(new NewCategoryDto("Pizza")).getId();

        newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setCategory(categoryId);
        newEventDto.setEventDate(LocalDateTime.now().plusHours(3)); // Устанавливаем дату события через 3 часа

        eventService.create(userId, newEventDto);
        newEventDto.setTitle("Test Event 2");
        eventService.create(userId, newEventDto);

        Mockito.when(statisticClient.getStatistics(any(LocalDateTime.class), any(LocalDateTime.class),
                anyList(), anyBoolean())).thenReturn(Collections.emptyList());
    }

    @Test
    public void testCreateEvent() {
        EventDto createdEvent = eventService.create(userId, newEventDto);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo(newEventDto.getTitle());
        assertThat(createdEvent.getInitiator().getId()).isEqualTo(userId);

        Optional<Event> foundEvent = eventRepository.findById(createdEvent.getId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getTitle()).isEqualTo(newEventDto.getTitle());
    }

    @Test
    public void testUpdateEvent() {
        EventDto createdEvent = eventService.create(userId, newEventDto);

        AdminUpdateEventRequest updateEventDto = new AdminUpdateEventRequest();
        updateEventDto.setStateAction(AdminAction.PUBLISH_EVENT);
        updateEventDto.setEventDate(LocalDateTime.now().plusHours(4)); // Обновляем дату события

        EventDto updatedEvent = eventService.update(createdEvent.getId(), updateEventDto);

        assertThat(updatedEvent.getState()).isEqualTo(EventState.PUBLISHED);

        Event updatedEntity = eventRepository.findById(createdEvent.getId()).orElseThrow();
        assertThat(updatedEntity.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    public void testUpdateUserEvent() {
        EventDto createdEvent = eventService.create(userId, newEventDto);

        UserUpdateEventRequest updateUserDto = new UserUpdateEventRequest();
        var expectedDate = LocalDateTime.now().plusHours(5);
        updateUserDto.setEventDate(expectedDate); // Обновляем дату события

        EventDto updatedUserEvent = eventService.update(userId, createdEvent.getId(), updateUserDto);

        assertThat(updatedUserEvent.getEventDate()).isEqualTo(updateUserDto.getEventDate());

        updateUserDto.setEventDate(LocalDateTime.now());

        assertThrows(ConditionNotMetException.class, () -> eventService.update(userId, createdEvent.getId(), updateUserDto));

        Event updatedEntity = eventRepository.findById(createdEvent.getId()).orElseThrow();
        assertThat(updatedEntity.getEventDate()).isEqualTo(expectedDate);
    }


    @Test
    public void testGetById() {
        EventDto createdEvent = eventService.create(userId, newEventDto);
        AdminUpdateEventRequest dto = new AdminUpdateEventRequest();
        dto.setStateAction(AdminAction.PUBLISH_EVENT);
        eventService.update(createdEvent.getId(), dto);
        EventDto foundEvent = eventService.getById(createdEvent.getId());

        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getId()).isEqualTo(createdEvent.getId());
        assertThat(foundEvent.getTitle()).isEqualTo(newEventDto.getTitle());
    }

    @Test
    public void testGetByIdAsUser() {
        EventDto createdEvent = eventService.create(userId, newEventDto);

        EventDto foundEvent = eventService.getById(userId, createdEvent.getId());

        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getInitiator().getId()).isEqualTo(userId);
    }

    @Test
    public void testGetAllWithFilters() {
        List<EventDto> events = eventService.getAll(
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
                EventSortOption.EVENT_DATE,
                0,
                10
        );

        assertThat(events).isNotEmpty();
        assertThat(events.size()).isGreaterThan(0); // Ожидаем, что хотя бы одно событие будет возвращено
    }
}