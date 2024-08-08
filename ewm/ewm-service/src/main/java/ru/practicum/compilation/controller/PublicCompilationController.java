package ru.practicum.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Validated
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
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return {@link ResponseEntity} содержащий список {@link CompilationDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(value = "pinned", required = false) Optional<Boolean> pinned,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {

        log.debug("Endpoint GET /compilations has been reached with pinned: {}, from: {}, size: {}",
                pinned.orElse(null), from, size);

        List<CompilationDto> compilations = compilationService.getAll(pinned, from, size);
        log.info("Compilations fetched successfully");
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
        log.debug("Endpoint GET /compilations/{} has been reached", compId);
        CompilationDto compilation = compilationService.getById(compId);
        log.info("Compilation {} fetched successfully", compId);
        return ResponseEntity.ok(compilation);

    }
}
