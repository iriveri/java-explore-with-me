package ru.practicum.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @Autowired
    public PublicCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    /**
     * Получение подборок событий.
     * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список.
     *
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(value = "pinned", required = false) Optional<Boolean> pinned,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size){

        log.debug("Endpoint /compilations has been reached with pinned: {}, from: {}, size: {}",
                pinned.orElse(null), from, size);

        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        return ResponseEntity.ok(compilations);
    }

    /**
     * Получение подборки событий по его id.
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404.
     *
     * @param compId id подборки
     * @return {@link ResponseEntity} содержащий {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        log.debug("Endpoint /compilations/{} has been reached", compId);

        Optional<CompilationDto> compilation = compilationService.getCompilationById(compId);

        if (compilation.isPresent()) {
            return ResponseEntity.ok(compilation.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Replace with a proper error response if needed
        }
    }
}
