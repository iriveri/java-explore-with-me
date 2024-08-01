package ru.practicum.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.StatisticsService;
import ru.practicum.category.AdminCategoryController;
import ru.practicum.category.PublicCategoryController;
import ru.practicum.dto.Location;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.event.AdminEventController;
import ru.practicum.event.EventService;
import ru.practicum.event.PrivateEventController;
import ru.practicum.event.PublicEventController;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminEventController.class, PublicEventController.class, PrivateEventController.class, GlobalExceptionHandler.class})
public class EventControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private StatisticsService statisticsService;

    @BeforeEach
    @Autowired
    public void setUp(EventService eventService, StatisticsService statisticsService) {
        EventFullDto event = new EventFullDto();
        event.setId(1L);
        Mockito.when(eventService.addEvent(anyLong(), any(NewEventDto.class))).thenReturn(event);
    }
    @Test
    public void testPrivateValidation(){
        NewEventDto invalidEventDto = new NewEventDto();
        // Leaving some required fields null or empty
        invalidEventDto.setAnnotation("Short annotation");
        invalidEventDto.setCategory(1L);
        invalidEventDto.setDescription("Short description");
        invalidEventDto.setEventDate(LocalDateTime.now().minusDays(1)); // Invalid date
        invalidEventDto.setLocation(new Location(1, 1));
        invalidEventDto.setParticipantLimit(-1); // Invalid participant limit
        invalidEventDto.setTitle("Title");

        assertDoesNotThrow(() -> {
            mockMvc.perform(post("/users/1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEventDto)))
                    .andExpect(status().isBadRequest());
        }, "Обращение с неверными данными должно вызывать ошибку");
    }
}
