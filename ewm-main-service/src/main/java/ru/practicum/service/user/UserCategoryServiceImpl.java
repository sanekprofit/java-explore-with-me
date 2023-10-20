package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.repository.category.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCategoryServiceImpl implements UserCategoryService {

    private final CategoryRepository repository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadParamException("Incorrect pagination params.");
        }
        List<CategoryDto> dtos = new ArrayList<>();
        Slice<Category> slice = repository.findAll(PageRequest.of(from, size));
        for (Category category : slice) {
            dtos.add(CategoryMapper.toCategoryDto(category.getId(), category.getName()));
        }
        return dtos;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        if (catId == null || catId < 1) {
            throw new BadParamException("Incorrect type of category id.");
        }
        Optional<Category> categoryOpt = repository.findById(catId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format("Category with id %d not found", catId));
        }
        Category category = categoryOpt.get();
        return CategoryMapper.toCategoryDto(category.getId(), category.getName());
    }
}