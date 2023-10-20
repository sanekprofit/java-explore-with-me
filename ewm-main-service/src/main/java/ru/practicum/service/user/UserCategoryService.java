package ru.practicum.service.user;

import ru.practicum.model.category.dto.CategoryDto;

import java.util.List;

public interface UserCategoryService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);

}