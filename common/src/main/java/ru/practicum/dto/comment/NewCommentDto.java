package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotNull
    private Long eventId;
    @Size(min = 1, max = 200)
    @NotBlank
    private String text;
}
