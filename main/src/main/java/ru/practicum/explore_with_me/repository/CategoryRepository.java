package ru.practicum.explore_with_me.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore_with_me.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT COUNT(c) FROM Category c WHERE c.name=:name")
    Long getCountByName(String name);

    @Query("SELECT c from Category c")
    Page<Category> getAll(Integer from, Integer size, Pageable pageable);
}
