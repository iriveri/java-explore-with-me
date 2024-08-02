package ru.practicum.category;

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
import ru.practicum.category.controller.AdminCategoryController;
import ru.practicum.category.controller.PublicCategoryController;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AdminCategoryController.class, PublicCategoryController.class, GlobalExceptionHandler.class})
class CategoryControllersValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        Mockito.when(categoryService.getAll(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        CategoryDto category = new CategoryDto(1L, "lol");
        Mockito.when(categoryService.create(any(NewCategoryDto.class))).thenReturn(category);
        Mockito.when(categoryService.update(eq(1L), any(UpdateCategoryDto.class))).thenReturn(category);
    }

    @Test
    public void testPublicValidation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("from", "10")
                        .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());

        // Additional edge cases
        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("from", "1000000")
                        .param("size", "100"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPrivatePostValidation() throws Exception {
        String invalidNameCategory = "{\"name\": \"\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidNameCategory))
                .andExpect(status().isBadRequest());

        String validCategory = "{\"name\": \"name\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCategory))
                .andExpect(status().isCreated());

        // Additional invalid JSON
        String invalidJson = "{ name: \"name\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Unique name test
        Mockito.when(categoryService.create(any(NewCategoryDto.class)))
                .thenThrow(new ConditionNotMetException("Category name must be unique"));
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCategory))
                .andExpect(status().isConflict());
    }

    @Test
    public void testPrivatePatchValidation() throws Exception {
        String invalidNameCategory = "{\"name\": \"\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidNameCategory))
                .andExpect(status().isBadRequest());

        String validNoNameCategory = "{}";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validNoNameCategory))
                .andExpect(status().isOk());

        String validCategory = "{\"name\": \"name\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCategory))
                .andExpect(status().isOk());

        // Additional invalid JSON
        String invalidJson = "{ name: \"name\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Mockito.doThrow(new ConditionNotMetException("Category cannot be deleted as it is associated with events"))
                .when(categoryService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/categories/1"))
                .andExpect(status().isConflict());

        Mockito.doNothing().when(categoryService).delete(2L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/categories/2"))
                .andExpect(status().isNoContent());
    }
}