package ru.practicum.model.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {

    @NotBlank
    @Size(max = 2000, min = 1)
    private String text;

}