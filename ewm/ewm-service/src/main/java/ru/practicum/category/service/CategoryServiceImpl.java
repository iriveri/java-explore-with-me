package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepo;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo repo;
    private final CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepo repo, CategoryMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = mapper.fromDto(newCategoryDto);
        return mapper.toDto(repo.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto categoryDto) {
        Category category = getCategoryById(catId);
        category.setName(categoryDto.getName());
        return mapper.toDto(repo.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        repo.deleteById(catId);
    }

    @Override
    public Category getCategoryById(Long catId) {

        Optional<Category> category = repo.findById(catId);
        if (category.isEmpty())
            throw new RuntimeException();

        return category.get();
    }

    @Override
    public CategoryDto getCategoryDtoById(Long catId) {
        return mapper.toDto(getCategoryById(catId));
    }


    @Override
    public List<CategoryDto> getCategories(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return repo.findAll(pageRequest).getContent().stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
