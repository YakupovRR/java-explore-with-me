package ru.practicum.explore_with_me.model.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class CompilationInDto {

    @NotBlank
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}
