package ru.practicum.explore_with_me.model.dto;

import lombok.Data;
import ru.practicum.explore_with_me.model.dto.validation.Create;
import ru.practicum.explore_with_me.model.dto.validation.Update;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank(groups = {Create.class, Update.class})
    private String name;
}
