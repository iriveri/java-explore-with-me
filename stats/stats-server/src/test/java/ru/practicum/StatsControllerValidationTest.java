package ru.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.dto.statistics.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StatsController.class, CustomExceptionHandler.class})
@AutoConfigureMockMvc
public class StatsControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    @BeforeAll
    public static void setUp(@Autowired StatsService statsService) {
        Mockito.doNothing().when(statsService).createRecord(any(EndpointHitDto.class));
        Mockito.when(statsService.getStatistics(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(List.class),
                any(boolean.class)
        )).thenReturn(Collections.emptyList());
    }

    @Test
    public void testValidRequest() throws Exception {
        String validRequest = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testMissingAppField() throws Exception {
        String missingApp = "{ \"uri\": \"/test\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingApp))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingUriField() throws Exception {
        String missingUri = "{ \"app\": \"testApp\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingUri))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingIpField() throws Exception {
        String missingIp = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingIp))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidIpv4() throws Exception {
        String invalidIpv4 = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"999.999.999.999\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidIpv4))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIpv6() throws Exception {
        String ipv6 = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"2001:0db8:85a3:0000:0000:8a2e:0370:7334\", \"timestamp\": \"2023-01-01 10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ipv6))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidTimestampFormat() throws Exception {
        String invalidTimestamp = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2023-01-01T10:00:00\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTimestamp))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDateFormat() throws Exception {
        String start = "2023-01-01T10:00:00";
        String end = "2023-01-02T10:00:00";
        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDateSequence() throws Exception {
        String start = "2023-01-02 10:00:00";
        String end = "2023-01-01 10:00:00";
        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isBadRequest());
    }
}
