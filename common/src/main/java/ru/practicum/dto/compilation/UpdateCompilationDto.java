package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.UniqueElements;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    @UniqueElements
    List<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
