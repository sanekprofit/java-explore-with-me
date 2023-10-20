package ru.practicum.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}