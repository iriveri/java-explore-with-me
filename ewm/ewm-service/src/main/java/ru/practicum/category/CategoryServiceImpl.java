package ru.practicum.category;

import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(Long catId) {

    }

    @Override
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto categoryDto) {
        return null;
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return List.of();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return null;
    }
}
