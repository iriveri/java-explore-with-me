package ru.practicum.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
    public void setUp(@Autowired CompilationService compilationService) {
        Mockito.when(compilationService.getCompilations(
                any(Optional.class),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.emptyList());

        CompilationDto compilation = new CompilationDto(null,1L,true,"lol");

        Mockito.when(compilationService.addCompilation(any(NewCompilationDto.class)))
                .thenReturn(compilation);

        Mockito.when(compilationService.updateCompilation(eq(1L), any(UpdateCompilationDto.class)))
                .thenReturn(compilation);
    }

    @Test
    public void testPublicValidation() throws Exception {
        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/compilations"))
                    .andExpect(status().isOk());
        }, "Обращение к эндпоинту /compilations без параметров не должно взывать ошибок");

        assertDoesNotThrow(() -> {
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                            .param("pinned", "true")
                            .param("from", "10")
                            .param("size", "10"))
                    .andExpect(status().isOk());
        }, "Получение подборки с правельными параметрами не должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                            .param("pinned", "")
                            .param("from", "")
                            .param("size", ""))
                    .andExpect(status().isOk());
        }, "Обращение к эндпоинту /compilations с пустыми параметрами не должно взывать ошибок");

        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                            .param("from", "-1")
                            .param("size", "-10"))
                    .andExpect(status().isNotFound());
        }, "Получение подборки с не правельными параметрами должно приводить к ошибке");
    }

    @Test
    public void testPrivatePostValidation() throws Exception {
        assertDoesNotThrow(() -> {
            String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": \"true\", \"title\": \"title\" }";
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidEventsCompilation))
                    .andExpect(status().isConflict());
        }, "Добавление одинаковых событий в подборке должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String invalidTitleCompilation = "{ \"events\": [1,2] }";
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidTitleCompilation))
                    .andExpect(status().isConflict());
        }, "Добавление подборки без названия должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validCompilation = "{ \"events\": [1,2], \"title\": \"title\" }";
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCompilation))
                    .andExpect(status().isCreated());
        }, "Добавление правильной подборки не должно приводить к ошибке");
    }

    @Test
    public void testPrivatePatchValidation() throws Exception {
        assertDoesNotThrow(() -> {
            String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": \"true\", \"title\": \"title\" }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidEventsCompilation))
                    .andExpect(status().isConflict());
        }, "При изменении в подборке не должно быть одинаковых событий - должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validEventsCompilation = "{ \"events\": [1,2] }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validEventsCompilation))
                    .andExpect(status().isOk());
        }, "Измененией подборки без названия не должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validTitleCompilation = "{ \"title\": \"title\" }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validTitleCompilation))
                    .andExpect(status().isOk());
        }, "Добавление правильной подборки не должно приводить к ошибке");
    }
}


