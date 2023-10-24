package ru.practicum.service.admin;

import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto postCategory(NewCategoryDto categoryDto);

    void deleteCategory(Integer catId);

    CategoryDto patchCategory(NewCategoryDto categoryDto, Integer catId);

}