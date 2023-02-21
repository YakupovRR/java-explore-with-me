package ru.practicum.explore_with_me.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.model.Category;
import ru.practicum.explore_with_me.model.dto.CategoryDto;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public Category toCategory(Long id, CategoryDto categoryDto) {
        Category category = new Category();
        if (id == null) {
            category.setId(categoryDto.getId());
        } else {
            category.setId(id);
        }
        category.setName(categoryDto.getName());
        return category;
    }
}
