package ru.practicum.event;

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
import ru.practicum.NotFoundException;
import ru.practicum.StatisticClient;
import ru.practicum.dto.event.*;
import ru.practicum.event.controller.AdminEventController;
import ru.practicum.event.controller.PrivateEventController;
import ru.practicum.event.controller.PublicEventController;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PublicEventController.class, PrivateEventController.class, AdminEventController.class, GlobalExceptionHandler.class})
class EventControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private StatisticClient statisticClient;

    @BeforeEach
    public void setUp() {
        EventFullDto event = new EventFullDto();
        event.setId(1L);
        EventShortDto shortEvent = new EventShortDto();
        shortEvent.setId(1L);

        Mockito.when(eventService.create(eq(1L), any(NewEventDto.class)))
                .thenReturn(event);
        Mockito.when(eventService.update(eq(1L), any(UpdateEventAdminDto.class)))
                .thenReturn(event);
        Mockito.when(eventService.update(eq(1L), eq(1L), any(UpdateEventUserDto.class)))
                .thenReturn(event);
        Mockito.when(eventService.getById(eq(1L)))
                .thenReturn(event);
        Mockito.when(eventService.getById(eq(1L), eq(1L)))
                .thenReturn(event);
        Mockito.when(eventService.getByUserId(eq(1L), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(shortEvent));

        Mockito.when(eventService.getAll(
                anyList(),
                anyList(),
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.singletonList(event));

        Mockito.when(eventService.getAll(
                anyString(),
                anyList(),
                anyBoolean(),
                any(),
                any(),
                anyBoolean(),
                any(),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.singletonList(shortEvent));


    }

    @Test
    public void testAdminGetEvents() throws Exception {
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
    public void testAdminUpdateEvent() throws Exception {
        // Valid request
        String validRequest = "{ \"annotation\": \"Valid long annotation\", \"category\": 1, \"description\": \"Valid long description\", " +
                "\"eventDate\": \"2025-01-01 00:00:01\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk());

        // Invalid annotation
        String invalidAnnotationRequest = "{ \"annotation\": \"Short\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAnnotationRequest))
                .andExpect(status().isBadRequest());

        // Invalid title
        String invalidTitleRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"PUBLISH_EVENT\", " +
                "\"title\": \"Ti\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTitleRequest))
                .andExpect(status().isBadRequest());

        // Invalid stateAction
        String invalidStateActionRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"stateAction\": \"INVALID_ACTION\", " +
                "\"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStateActionRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPrivateGetUserEvents() throws Exception {
        // Test with no parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events"))
                .andExpect(status().isOk());

        // Test with valid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Test with invalid 'from' and 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());

        // Test with out of range 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddEvent() throws Exception {
        // Valid request
        String validRequest = "{ \"annotation\": \"Valid long annotation\", \"category\": 1, \"description\": \"Valid long description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());


        // Invalid title
        String invalidTitleRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"title\": \"Ti\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTitleRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPrivateGetEvent() throws Exception {
        // Valid request
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/1"))
                .andExpect(status().isOk());

        // Event not found
        Mockito.when(eventService.getById(eq(1L), eq(2L)))
                .thenThrow(new NotFoundException("Event not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        // Valid request
        String validRequest = "{ \"annotation\": \"Valid long annotation\", \"category\": 1, \"description\": \"Valid long description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"title\": \"Valid title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk());

        // Invalid title
        String invalidTitleRequest = "{ \"annotation\": \"Valid annotation\", \"category\": 1, \"description\": \"Valid description\", " +
                "\"eventDate\": \"2025-01-01 00:00:00\", \"location\": {\"lat\": 55.7558, \"lon\": 37.6176}, " +
                "\"paid\": true, \"participantLimit\": 100, \"requestModeration\": true, \"title\": \"Ti\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTitleRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPublicGetEvents() throws Exception {
        // Test with no parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/events"))
                .andExpect(status().isOk());

        // Test with valid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .param("text", "sample")
                        .param("categories", "1", "2")
                        .param("paid", "true")
                        .param("rangeStart", "2024-01-01 00:00:00")
                        .param("rangeEnd", "2024-12-31 23:59:59")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Test with invalid 'from' and 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());

        // Test with out of range 'size'
        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPublicGetEvent() throws Exception {
        // Valid request
        mockMvc.perform(MockMvcRequestBuilders.get("/events/1"))
                .andExpect(status().isOk());

        // Event not found
        Mockito.when(eventService.getById(eq(2L)))
                .thenThrow(new NotFoundException("Event not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/events/2"))
                .andExpect(status().isNotFound());

    }
}