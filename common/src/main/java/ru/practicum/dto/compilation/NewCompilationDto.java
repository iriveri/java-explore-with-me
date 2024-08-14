package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.UniqueElements;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @UniqueElements
    List<Long> events;
    Boolean pinned = false;
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
}
