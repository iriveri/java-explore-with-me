package ru.practicum.compilation.controller;

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
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import jakarta.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    public AdminCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    /**
     * Добавление новой подборки (подборка может не содержать событий).
     *
     * @param newCompilation {@link NewCompilationDto} данные новой подборки
     * @return {@link ResponseEntity} содержащий {@link CompilationDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<CompilationDto> saveCompilation(@Valid @RequestBody NewCompilationDto newCompilation) {
        log.debug("Endpoint POST /admin/compilations has been reached with NewCompilationDto: {}", newCompilation);
        CompilationDto createdCompilation = compilationService.create(newCompilation);
        log.info("Compilation {} created successfully", createdCompilation.getId());
        return new ResponseEntity<>(createdCompilation, HttpStatus.CREATED);
    }

    /**
     * Удаление подборки
     *
     * @param compilationId идентификатор подборки
     * @return {@link ResponseEntity} с статусом ответа {@link HttpStatus#NO_CONTENT}
     */
    @DeleteMapping("/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compilationId) {
        log.debug("Endpoint DELETE /admin/compilations/{} has been reached", compilationId);
        compilationService.delete(compilationId);
        log.info("Compilation {} deleted successfully", compilationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Обновить информация о подборке
     *
     * @param compilationId  идентификатор подборки
     * @param compilationDto {@link UpdateCompilationDto} данные для обновления подборки
     * @return {@link ResponseEntity} содержащий обновленный {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{compilationId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compilationId,
                                                            @Valid @RequestBody UpdateCompilationDto compilationDto) {

        log.debug("Endpoint PATCH /admin/compilations/{} has been reached with UpdateCompilationRequest: {}",
                compilationId, compilationDto);

        CompilationDto updatedCompilation = compilationService.update(compilationId, compilationDto);
        log.info("Compilation {} patched successfully", compilationId);
        return ResponseEntity.ok(updatedCompilation);
    }
}