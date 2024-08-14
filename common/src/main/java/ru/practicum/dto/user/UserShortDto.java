package ru.practicum.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @NotNull
    private long id;  //readOnly: true
    @NotNull
    private String name;
}
