package ru.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)

public class StatsControllerIT {

    private final MockMvc mockMvc;

    @Autowired
    public StatsControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateRecord() throws Exception {
        String jsonRequest = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2024-07-01 10:00:00\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", "2024-07-01 00:00:00")
                        .param("end", "2024-07-31 23:59:59")
                        .param("uris", "/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri").value("/test"))
                .andExpect(jsonPath("$[0].hits").value(1));
    }

    @Test
    public void testCreateRecordWithoutUris() throws Exception {
        String jsonRequest = "{ \"app\": \"testApp\", \"uri\": \"/test\", \"ip\": \"192.168.1.1\", \"timestamp\": \"2024-07-01 10:00:00\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", "2024-07-01 00:00:00")
                        .param("end", "2024-07-31 23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri").value("/test"))
                .andExpect(jsonPath("$[0].hits").value(2));
    }
}
