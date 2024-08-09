package ru.practicum.request;

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
import ru.practicum.dto.requests.EventRequestStatusUpdateCommand;
import ru.practicum.dto.requests.EventRequestStatusUpdateResponse;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.controller.ParticipationRequestsController;
import ru.practicum.request.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ParticipationRequestsController.class, GlobalExceptionHandler.class})
class RequestControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipationRequestService participationRequestService;

    @BeforeEach
    public void setUp() {
        ParticipationRequestDto requestDto = new ParticipationRequestDto(LocalDateTime.now(), 1L, 1L, 1L, RequestStatus.PENDING);
        List<ParticipationRequestDto> requestDtoList = Collections.singletonList(requestDto);
        EventRequestStatusUpdateCommand updateRequest = new EventRequestStatusUpdateCommand(Collections.singletonList(1L), RequestStatus.CONFIRMED);
        EventRequestStatusUpdateResponse updateResult = new EventRequestStatusUpdateResponse(Collections.singletonList(requestDto), Collections.emptyList());

        Mockito.when(participationRequestService.getByUserId(eq(1L)))
                .thenReturn(requestDtoList);

        Mockito.when(participationRequestService.create(eq(1L), eq(1L)))
                .thenReturn(requestDto);

        Mockito.when(participationRequestService.delete(eq(1L), eq(1L)))
                .thenReturn(requestDto);

        Mockito.when(participationRequestService.getByEventId(eq(1L), eq(1L)))
                .thenReturn(requestDtoList);

        Mockito.when(participationRequestService.updateStatus(eq(1L), eq(1L), any()))
                .thenReturn(updateResult);
    }

    @Test
    public void testGetUserRequests() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testAddParticipationRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        Mockito.when(participationRequestService.create(eq(1L), eq(2L)))
                .thenThrow(new ConditionNotMetException("Error: request conflict"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/requests")
                        .param("eventId", "2"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCancelRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        Mockito.when(participationRequestService.delete(eq(1L), eq(2L)))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/requests/2/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEventParticipants() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testChangeRequestStatus() throws Exception {
        EventRequestStatusUpdateCommand updateRequest = new EventRequestStatusUpdateCommand(Collections.singletonList(1L), RequestStatus.CONFIRMED);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(1L));

        Mockito.when(participationRequestService.updateStatus(eq(1L), eq(2L), any()))
                .thenThrow(new ConditionNotMetException("Error: status conflict"));
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/events/2/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }
}

