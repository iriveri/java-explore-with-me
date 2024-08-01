package ru.practicum.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewUserRequest {
    @NotNull
    @Email
    String email;
    @NotNull
    @Size(min = 2, max = 250)
    String name;
}
