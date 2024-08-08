package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Добавление новой категории.
     * Имя категории должно быть уникальным
     *
     * @param newCategory {@link NewCategoryDto} данные новой подборки
     * @return {@link ResponseEntity} содержащий {@link CategoryDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCategory) {
        log.debug("Endpoint POST /admin/categories has been reached with NewCategoryDto: {}", newCategory);
        CategoryDto createdCategory = categoryService.create(newCategory);
        log.info("Category {} created successfully", createdCategory.getId());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Удаление категории.
     * С категорией не должно быть связано ни одного события.
     *
     * @param categoryId идентификатор категории
     * @return {@link ResponseEntity} с статусом ответа {@link HttpStatus#NO_CONTENT}
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.debug("Endpoint DELETE /admin/categories/{} has been reached", categoryId);
        categoryService.delete(categoryId);
        log.info("Category {} deleted successfully", categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Изменение категории.
     * Имя категории должно быть уникальным
     *
     * @param catId       идентификатор категории
     * @param categoryDto {@link UpdateCategoryDto} данные для обновления категории
     * @return {@link ResponseEntity} содержащий обновленный {@link CategoryDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @Valid @RequestBody UpdateCategoryDto categoryDto) {

        log.debug("Endpoint PATCH /admin/categories/{} has been reached with CategoryDto: {}",
                catId, categoryDto);

        CategoryDto updatedCategory = categoryService.update(catId, categoryDto);
        log.info("Category {} patched successfully", catId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }
}