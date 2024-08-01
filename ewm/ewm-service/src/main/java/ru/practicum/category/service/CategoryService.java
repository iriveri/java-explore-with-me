package ru.practicum.category.service;

import ru.practicum.category.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(Long catId, UpdateCategoryDto categoryDto);

    void delete(Long catId);

    Category getEntityById(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(int offset, int limit);
}
