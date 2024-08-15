package ru.practicum.category.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.fromDto(newCategoryDto);
        return categoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, UpdateCategoryDto categoryDto) {
        Category category = getEntityById(catId);
        category.setName(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId))
            throw new NotFoundException("Category with id=" + categoryId + " was not found");

        if (eventRepository.existsByCategoryId(categoryId))
            throw new ConditionNotMetException("Category with id=" + categoryId + " is in use");

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category getEntityById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        return categoryMapper.toDto(getEntityById(categoryId));
    }


    @Override
    public List<CategoryDto> getAll(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return categoryRepository.findAll(pageRequest).getContent().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
