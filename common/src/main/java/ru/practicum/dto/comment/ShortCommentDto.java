package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortCommentDto {
    private Long id;
    private Long eventId;
    private LocalDateTime createdAt;
    private String text;
}
