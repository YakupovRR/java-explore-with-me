package ru.practicum.explore_with_me.service.category;

import ru.practicum.explore_with_me.model.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category);

    Category updateCategory(Category category);

    Category getCategoryById(long id);

    List<Category> getAllCategories(Integer from, Integer size);

    void removeCategory(long id);
}
