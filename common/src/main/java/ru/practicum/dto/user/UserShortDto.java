package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @NotNull
    private long id;  //readOnly: true
    @NotNull
    private String name;
}
