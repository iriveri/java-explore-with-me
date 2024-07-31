package ru.practicum.compilation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.UniqueElements;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AdminCompilationController.class, PublicCompilationController.class, GlobalExceptionHandler.class})
class CompilationControllersValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    @BeforeAll
    public static void setUp(@Autowired CompilationService compilationService) {
        Mockito.when(compilationService.getCompilations(
                any(Optional.class),
                anyInt(),
                anyInt()
        )).thenReturn(Collections.emptyList());

        CompilationDto compilation = new CompilationDto();

        Mockito.when(compilationService.saveCompilation(any(NewCompilationDto.class)))
                .thenReturn(compilation);

        Mockito.when(compilationService.updateCompilation(eq(1L), any(UpdateCompilationRequest.class)))
                .thenReturn(compilation);
    }

    @Test
    public void testPublicValidation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("pinned", "true")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/compilations")
                        .param("from", "-1")
                        .param("size", "-10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPrivatePostValidation() throws Exception {

        String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": \"true\", \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                        .andExpect(status().isBadRequest());

        String invalidTitleCompilation = "{ \"events\": [1,2] }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isBadRequest());

        String validCompilation = "{ \"events\": [1,2], \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isCreated());
    }
    @Test
    public void testPrivatePatchValidation() throws Exception {

        String invalidEventsCompilation = "{ \"events\": [1,1], \"pinned\": \"true\", \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isBadRequest());

        String invalidTitleCompilation = "{ \"events\": [1,2] }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isBadRequest());

        String validCompilation = "{ \"events\": [1,2], \"title\": \"title\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEventsCompilation))
                .andExpect(status().isOk());
    }

}


