package ru.practicum.compilation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@RestController
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
    public ResponseEntity<CompilationDto> saveCompilation(@RequestBody NewCompilationDto newCompilation) {
        CompilationDto savedCompilation = compilationService.saveCompilation(newCompilation);
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
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Обновить информация о подборке
     *
     * @param compId идентификатор подборки
     * @param updateCompilationRequest данные для обновления подборки
     * @return {@link ResponseEntity} содержащий обновленный {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId, @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        CompilationDto updatedCompilation = compilationService.updateCompilation(compId, updateCompilationRequest);
        return new ResponseEntity<>(updatedCompilation, HttpStatus.OK);
    }
}