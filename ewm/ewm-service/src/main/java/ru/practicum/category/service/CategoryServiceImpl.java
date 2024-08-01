package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ConditionNotMetException;
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
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo repo;
    private final EventRepo eventRepo;
    private final CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepo repo, EventRepo eventRepo, CategoryMapper mapper) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = mapper.fromDto(newCategoryDto);
        return mapper.toDto(repo.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, UpdateCategoryDto categoryDto) {
        Category category = getEntityById(catId);
        category.setName(categoryDto.getName());
        return mapper.toDto(repo.save(category));
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        if (!repo.existsById(catId))
            throw new NotFoundException("Category with id=" + catId + " was not found");

        if (eventRepo.existsByCategoryId(catId))
            throw new ConditionNotMetException("The category is not empty");

        repo.deleteById(catId);
    }

    @Override
    public Category getEntityById(Long catId) {
        return repo.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));
    }

    @Override
    public CategoryDto getById(Long catId) {
        return mapper.toDto(getEntityById(catId));
    }


    @Override
    public List<CategoryDto> getAll(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return repo.findAll(pageRequest).getContent().stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
