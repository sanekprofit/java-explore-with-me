package ru.practicum.model.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {

    @NotBlank
    @NotNull
    @Size(max = 50, min = 1)
    private String name;

}