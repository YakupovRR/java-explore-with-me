package ru.practicum.explore_with_me.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.CategoryDto;
import ru.practicum.explore_with_me.model.dto.validation.Create;
import ru.practicum.explore_with_me.model.dto.validation.Update;
import ru.practicum.explore_with_me.model.mapper.CategoryMapper;
import ru.practicum.explore_with_me.service.category.CategoryService;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Validated(Create.class) CategoryDto categoryDto) {
        log.info("Получен Post запрос к эндпоинту admin/categories");
        return new ResponseEntity<>(
                categoryMapper.toDto(categoryService.addCategory(categoryMapper.toCategory(null, categoryDto))),
                HttpStatus.CREATED);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Validated(Update.class) CategoryDto categoryDto) {
        log.info("Получен Patch запрос к эндпоинту admin/categories", catId);
        return categoryMapper.toDto(categoryService.updateCategory(categoryMapper.toCategory(catId, categoryDto)));
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable long catId) {
        log.info("Получен Delete запрос к эндпоинту admin/categories", catId);
        categoryService.removeCategory(catId);
    }

}
