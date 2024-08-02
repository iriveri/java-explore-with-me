package ru.practicum.event;

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

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.event.LocationDto;
import ru.practicum.dto.event.UpdateEventAdminDto;
import ru.practicum.event.controller.AdminEventController;
import ru.practicum.event.service.EventService;
import ru.practicum.event.controller.PrivateEventController;
import ru.practicum.event.controller.PublicEventController;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminEventController.class, GlobalExceptionHandler.class})
class EventControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        EventFullDto event = new EventFullDto();
        event.setId(1L);

        Mockito.when(eventService.getAll(
                anyList(),
                anyList(),
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.singletonList(event));

        Mockito.when(eventService.update(eq(1L), any(UpdateEventAdminDto.class)))
                .thenReturn(event);
    }

    @Test
    public void testGetEvents() throws Exception {
        // Test with no parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events"))
                .andExpect(status().isOk());

        // Test with valid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PENDING", "PUBLISHED")
                        .param("categories", "1", "2")
                        .param("rangeStart", "2024-01-01 00:00:00")
                        .param("rangeEnd", "2024-12-31 23:59:59")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Test with invalid date format
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events")
                        .param("rangeStart", "invalid-date")
                        .param("rangeEnd", "invalid-date"))
                .andExpect(status().isBadRequest());

        // Test with invalid 'from' and 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());

        // Test with out of range 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        // Valid request
        String validRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:01\", \"locationDto\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk());

        // Invalid annotation
        String invalidAnnotationRequest = "{ \"annotation\": \"Short\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2024-01-01 00:00:00\", \"locationDto\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAnnotationRequest))
                .andExpect(status().isBadRequest());

        // Invalid eventDate (in the past)
        String invalidEventDateRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2023-01-01 00:00:00\", \"locationDto\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventDateRequest))
                .andExpect(status().isConflict());

        // Invalid title
        String invalidTitleRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2024-01-01 00:00:00\", \"locationDto\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Ti\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTitleRequest))
                .andExpect(status().isBadRequest());

        // Invalid stateAction
        String invalidStateActionRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2024-01-01 00:00:00\", \"locationDto\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"INVALID_ACTION\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStateActionRequest))
                .andExpect(status().isBadRequest());
    }
}
