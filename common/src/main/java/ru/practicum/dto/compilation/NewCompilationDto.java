package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    List<Integer> events;
    Boolean pinned;
    @Max(50)
    @Min(1)
    String title;
}
