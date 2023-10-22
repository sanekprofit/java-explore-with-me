package ru.practicum.mapper;

import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;

public class CategoryMapper {
    public static Category toCategory(String name) {
        return new Category(name);
    }

    public static CategoryDto toCategoryDto(int id, String name) {
        return new CategoryDto(id, name);
    }
}
