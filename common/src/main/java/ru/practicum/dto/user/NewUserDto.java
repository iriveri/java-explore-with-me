package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
