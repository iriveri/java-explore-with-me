package ru.practicum.compilation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import javax.validation.Valid;

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
        CompilationDto savedCompilation = compilationService.saveCompilation(newCompilation);
        log.info("Compilation {} created successfully",savedCompilation.getId());
        return new ResponseEntity<>(savedCompilation, HttpStatus.CREATED);
    }

    /**
     * Удаление подборки
     *
     * @param compId идентификатор подборки
     * @return {@link ResponseEntity} с статусом ответа {@link HttpStatus#NO_CONTENT}
     */
    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        log.debug("Endpoint DELETE /admin/compilations/{} has been reached", compId);
        compilationService.deleteCompilation(compId);
        log.info("Compilation {} deleted successfully", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Обновить информация о подборке
     *
     * @param compId идентификатор подборки
     * @param updateCompilationDto {@link UpdateCompilationDto} данные для обновления подборки
     * @return {@link ResponseEntity} содержащий обновленный {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {

        log.debug("Endpoint PATCH /admin/compilations/{} has been reached with UpdateCompilationRequest: {}",
                compId, updateCompilationDto);

        CompilationDto updatedCompilation = compilationService.updateCompilation(compId, updateCompilationDto);
        log.info("Compilation {} patched successfully", compId);
        return ResponseEntity.ok(updatedCompilation);
    }
}