package ru.practicum.explore_with_me.controller.publi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;
import ru.practicum.explore_with_me.model.mapper.CompilationMapper;
import ru.practicum.explore_with_me.service.compilation.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilation(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен Get запрос к эндпоинту compilations");
        return compilationService.getAllCompilation(pinned, from, size).stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("Получен Get запрос к эндпоинту compilations/" + compId);
        return compilationMapper.toDto(compilationService.getCompilationById(compId));
    }

}
