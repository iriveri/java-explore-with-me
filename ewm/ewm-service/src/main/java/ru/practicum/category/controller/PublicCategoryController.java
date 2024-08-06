package ru.practicum.category.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Получение категорий.
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список.
     *
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link CategoryDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size) {

        log.debug("Endpoint GET /categories has been reached with from: {}, size: {}", from, size);
        List<CategoryDto> categories = categoryService.getAll(from, size);
        log.info("Categories fetched successfully");
        return ResponseEntity.ok(categories);
    }

    /**
     * Получение категории событий по его id.
     * В случае, если категории с заданным id не найдено, возвращает статус код 404.
     *
     * @param catId id подборки
     * @return {@link ResponseEntity} содержащий {@link CategoryDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        log.debug("Endpoint GET /categories/{} has been reached", catId);
        CategoryDto category = categoryService.getById(catId);
        log.info("Category {} fetched successfully", catId);
        return ResponseEntity.ok(category);
    }
}