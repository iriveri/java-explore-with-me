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
import ru.practicum.category.controller.AdminCategoryController;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.controller.PublicCategoryController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AdminCategoryController.class, PublicCategoryController.class, GlobalExceptionHandler.class})
class CategoryControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService compilationService;

    @BeforeEach
    public void setUp(@Autowired CategoryService compilationService) {
        Mockito.when(compilationService.getAll(
                anyInt(),
                anyInt()
        )).thenReturn(Collections.emptyList());

        CategoryDto category = new CategoryDto(1L,"lol");

        Mockito.when(compilationService.create(any(NewCategoryDto.class)))
                .thenReturn(category);

        Mockito.when(compilationService.update(eq(1L), any(UpdateCategoryDto.class)))
                .thenReturn(category);
    }

    @Test
    public void testPublicValidation() throws Exception {
        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                    .andExpect(status().isOk());
        }, "Обращение к эндпоинту /categories без параметров не должно взывать ошибок");

        assertDoesNotThrow(() -> {
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                            .param("from", "10")
                            .param("size", "10"))
                    .andExpect(status().isOk());
        }, "Получение категорий с правельными параметрами не должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                            .param("from", "")
                            .param("size", ""))
                    .andExpect(status().isOk());
        }, "Обращение к эндпоинту /categories с пустыми параметрами не должно взывать ошибок");

        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                            .param("from", "-1")
                            .param("size", "-10"))
                    .andExpect(status().isNotFound());
        }, "Получение категории с не правельными параметрами должно приводить к ошибке");
    }

    @Test
    public void testPrivatePostValidation() throws Exception {
        assertDoesNotThrow(() -> {
            String invalidNameCategory = "{\"name\": \"\" }";
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidNameCategory))
                    .andExpect(status().isConflict());
        }, "Добавление категории без названия должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validCategory= "{\"name\": \"name\" }";
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCategory))
                    .andExpect(status().isCreated());
        }, "Добавление правильной категории не должно приводить к ошибке");
    }

    @Test
    public void testPrivatePatchValidation() throws Exception {
        assertDoesNotThrow(() -> {
            String invalidNameCategory = "{\"name\": \"\" }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidNameCategory))
                    .andExpect(status().isConflict());
        }, "При изменении категории c неверной длинной названия должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validNoNameCategory = "{ }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validNoNameCategory))
                    .andExpect(status().isOk());
        }, "Измененией категории без названия не должно приводить к ошибке");

        assertDoesNotThrow(() -> {
            String validCategory = "{\"name\": \"name\" }";
            mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCategory))
                    .andExpect(status().isOk());
        }, "Добавление правильной категории не должно приводить к ошибке");
    }
}