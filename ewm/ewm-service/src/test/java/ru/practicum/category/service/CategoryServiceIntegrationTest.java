package ru.practicum.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.NotFoundException;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepo;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;
import ru.practicum.event.EventRepo;

import javax.transaction.Transactional;

import java.util.List;




@Transactional // Обеспечивает откат транзакций после каждого теста
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private CategoryMapper mapper;

    private NewCategoryDto newCategoryDto;

    @BeforeEach
    public void setUp() {
        newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Test Category");
        // Установите другие поля, если они есть
    }

    @Test
    public void testCreateCategory() {
        CategoryDto createdCategory = categoryService.create(newCategoryDto);

        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo(newCategoryDto.getName());
        assertThat(createdCategory.getId()).isNotNull();

        // Проверяем, что категория сохранена в базе данных
        List<Category> categories = categoryRepo.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo(newCategoryDto.getName());
    }

    @Test
    public void testUpdateCategory() {
        CategoryDto createdCategory = categoryService.create(newCategoryDto);

        UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto();
        updateCategoryDto.setName("Updated Category");

        CategoryDto updatedCategory = categoryService.update(createdCategory.getId(), updateCategoryDto);

        assertThat(updatedCategory.getName()).isEqualTo(updateCategoryDto.getName());

        // Проверяем, что обновление прошло успешно в базе данных
        Category updatedEntity = categoryRepo.findById(createdCategory.getId()).orElseThrow();
        assertThat(updatedEntity.getName()).isEqualTo(updateCategoryDto.getName());
    }

    @Test
    public void testDeleteCategory() {
        CategoryDto createdCategory = categoryService.create(newCategoryDto);

        categoryService.delete(createdCategory.getId());

        // Проверяем, что категория была удалена из базы данных
        assertThrows(NotFoundException.class, () -> categoryService.getById(createdCategory.getId()));
    }

    @Test
    public void testGetAllCategories() {
        categoryService.create(newCategoryDto);

        List<CategoryDto> categories = categoryService.getAll(0, 10);

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo(newCategoryDto.getName());
    }
}