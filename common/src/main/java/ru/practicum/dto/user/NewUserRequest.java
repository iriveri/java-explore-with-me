package ru.practicum.dto.user;

import javax.validation.constraints.Email;

public class NewUserRequest {
    @Email
    String email;
    String name;
}
