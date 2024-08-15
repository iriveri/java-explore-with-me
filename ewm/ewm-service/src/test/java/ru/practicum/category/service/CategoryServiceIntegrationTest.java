package ru.practicum.category.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional // Обеспечивает откат транзакций после каждого теста
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private NewCategoryDto newCategoryDto;

    @BeforeEach
    public void setUp() {
        newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Test Category");
    }

    @Test
    public void testCreateCategory() {
        CategoryDto categoryDto = categoryService.create(newCategoryDto);

        assertThat(categoryDto).isNotNull();
        assertThat(categoryDto.getName()).isEqualTo(newCategoryDto.getName());
        assertThat(categoryDto.getId()).isNotNull();

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo(newCategoryDto.getName());
    }

    @Test
    public void testUpdateCategory() {
        CategoryDto categoryDto = categoryService.create(newCategoryDto);

        UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto();
        updateCategoryDto.setName("Updated Category");

        CategoryDto updatedCategory = categoryService.update(categoryDto.getId(), updateCategoryDto);

        assertThat(updatedCategory.getName()).isEqualTo(updateCategoryDto.getName());

        Category updatedEntity = categoryRepository.findById(categoryDto.getId()).orElseThrow();
        assertThat(updatedEntity.getName()).isEqualTo(updateCategoryDto.getName());
    }

    @Test
    public void testDeleteCategory() {
        CategoryDto categoryDto = categoryService.create(newCategoryDto);

        categoryService.delete(categoryDto.getId());

        assertThrows(NotFoundException.class, () -> categoryService.getById(categoryDto.getId()));
    }

    @Test
    public void testGetAllCategories() {
        categoryService.create(newCategoryDto);

        List<CategoryDto> categories = categoryService.getAll(0, 10);

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo(newCategoryDto.getName());
    }
}