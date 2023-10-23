package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;
import ru.practicum.repository.category.CategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository repository;

    @Override
    public CategoryDto postCategory(NewCategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto.getName());
        repository.save(category);

        return CategoryMapper.toCategoryDto(category.getId(), categoryDto.getName());
    }

    @Override
    public void deleteCategory(Integer catId) {
        if (catId == null || catId < 1) {
            throw new BadParamException("Incorrect type of category id.");
        }
        Optional<Category> category = repository.findById(catId);
        if (category.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        repository.deleteById(catId);
    }

    @Override
    public CategoryDto patchCategory(NewCategoryDto categoryDto, Integer catId) {
        if (catId == null || catId < 1) {
            throw new BadParamException("Incorrect type of category id.");
        }
        Optional<Category> categoryOpt = repository.findById(catId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        Category category = categoryOpt.get();

        if (categoryDto.getName() != null) category.setName(categoryDto.getName());
        repository.save(category);
        return CategoryMapper.toCategoryDto(category.getId(), categoryDto.getName());
    }
}