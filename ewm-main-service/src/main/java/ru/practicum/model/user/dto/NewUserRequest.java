package ru.practicum.model.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @Email
    @NotNull
    @Size(max = 255, min = 6)
    String email;

    @NotNull
    @NotBlank
    @Size(max = 255, min = 2)
    String name;

}