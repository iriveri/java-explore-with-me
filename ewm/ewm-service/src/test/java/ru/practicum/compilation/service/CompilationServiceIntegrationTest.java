package ru.practicum.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CompilationServiceIntegrationTest {

    @Autowired
    private CompilationServiceImpl compilationService;

    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private CompilationMapper compilationMapper;

    private NewCompilationDto newCompilationDto;

    @BeforeEach
    public void setUp() {
        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(false);
    }

    @Test
    public void testCreateCompilation() {
        CompilationDto createdCompilation = compilationService.create(newCompilationDto);

        assertThat(createdCompilation).isNotNull();
        assertThat(createdCompilation.getTitle()).isEqualTo(newCompilationDto.getTitle());
        assertThat(createdCompilation.getId()).isNotNull();

        List<Compilation> compilations = compilationRepository.findAll();
        assertThat(compilations).hasSize(1);
        assertThat(compilations.get(0).getTitle()).isEqualTo(newCompilationDto.getTitle());
    }

    @Test
    public void testUpdateCompilation() {
        CompilationDto createdCompilation = compilationService.create(newCompilationDto);

        UpdateCompilationDto updateCompilationDto = new UpdateCompilationDto();
        updateCompilationDto.setTitle("Updated Compilation");
        updateCompilationDto.setPinned(true);

        CompilationDto updatedCompilation = compilationService.update(createdCompilation.getId(), updateCompilationDto);

        assertThat(updatedCompilation.getTitle()).isEqualTo(updateCompilationDto.getTitle());

        Compilation updatedEntity = compilationRepository.findById(createdCompilation.getId()).orElseThrow();
        assertThat(updatedEntity.getTitle()).isEqualTo(updateCompilationDto.getTitle());
        assertThat(updatedEntity.getPinned()).isTrue();
    }

    @Test
    public void testDeleteCompilation() {
        CompilationDto createdCompilation = compilationService.create(newCompilationDto);
        compilationService.delete(createdCompilation.getId());

        assertThrows(NotFoundException.class, () -> compilationService.getById(createdCompilation.getId()));
    }

    @Test
    public void testGetAllCompilations() {
        compilationService.create(newCompilationDto);

        List<CompilationDto> compilations = compilationService.getAll(Optional.empty(), 0, 10);

        assertThat(compilations).hasSize(1);
        assertThat(compilations.get(0).getTitle()).isEqualTo(newCompilationDto.getTitle());
    }

    @Test
    public void testGetById() {
        CompilationDto createdCompilation = compilationService.create(newCompilationDto);

        CompilationDto foundCompilation = compilationService.getById(createdCompilation.getId());

        assertThat(foundCompilation).isNotNull();
        assertThat(foundCompilation.getId()).isEqualTo(createdCompilation.getId());
        assertThat(foundCompilation.getTitle()).isEqualTo(newCompilationDto.getTitle());
    }
}