package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    @NotNull
    @Email
    String email;
    @NotNull
    @Size(min = 2, max = 250)
    String name;
}
