package ru.practicum.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.ConditionNotMetException;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.compilation.controller.AdminCompilationController;
import ru.practicum.compilation.controller.PublicCompilationController;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AdminCompilationController.class, PublicCompilationController.class, GlobalExceptionHandler.class})
class CompilationControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    @BeforeEach
    public void setUp() {
        Mockito.when(compilationService.getAll(
                any(Optional.class),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.emptyList());

        CompilationDto compilation = new CompilationDto(null, 1L, true, "lol");

        Mockito.when(compilationService.create(any(NewCompilationDto.class)))
                .thenReturn(compilation);

        Mockito.when(compilationService.update(eq(1L), any(UpdateCompilationDto.class)))
                .thenReturn(compilation);
    }

    @Test
    public void testPublicValidation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("pinned", "true")
                        .param("from", "10")
                        .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("pinned", "")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());

        // Additional edge cases
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("from", "1000000")
                        .param("size", "100"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPrivatePostValidation() throws Exception {
        String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": true, \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isBadRequest());

        String invalidTitleCompilation = "{ \"events\": [1,2] }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTitleCompilation))
                .andExpect(status().isBadRequest());

        String validCompilation = "{ \"events\": [1,2], \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCompilation))
                .andExpect(status().isCreated());

        // Additional invalid JSON
        String invalidJson = "{ events: [1,2], title: title }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPrivatePatchValidation() throws Exception {
        String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": true, \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isBadRequest());

        String validEventsCompilation = "{ \"events\": [1,2] }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEventsCompilation))
                .andExpect(status().isOk());

        String validTitleCompilation = "{ \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTitleCompilation))
                .andExpect(status().isOk());

        // Additional invalid JSON
        String invalidJson = "{ events: [1,2], title: title }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCompilation() throws Exception {
        Mockito.doThrow(new ConditionNotMetException("Compilation cannot be deleted as it is associated with events"))
                .when(compilationService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/compilations/1"))
                .andExpect(status().isConflict());

        Mockito.doNothing().when(compilationService).delete(2L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/compilations/2"))
                .andExpect(status().isNoContent());
    }
}


