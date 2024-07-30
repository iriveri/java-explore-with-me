package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.UniqueElements;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @UniqueElements
    List<Integer> events;
    Boolean pinned = false;
    @Max(50)
    @Min(1)
    @NotNull
    String title;
}
