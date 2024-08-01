package ru.practicum.category.service;

import ru.practicum.category.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto categoryDto);

    void deleteCategory(Long catId);

    Category getCategoryById(Long catId);

    CategoryDto getCategoryDtoById(Long catId);

    List<CategoryDto> getCategories(int offset, int limit);
}
