package ru.practicum.explore_with_me.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationInDto;
import ru.practicum.explore_with_me.model.mapper.CompilationMapper;
import ru.practicum.explore_with_me.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationDto> addCompilation(@RequestBody @Valid CompilationInDto compilationDto) {
        log.info("Получен Post запрос к эндпоинту admin/compilations", compilationDto);
        return new ResponseEntity<>(compilationService.addCompilation(compilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable long compId, @RequestBody CompilationInDto dto) {
        log.info("Получен Patch запрос к эндпоинту admin/compilations/", compId);
        return compilationMapper.toDto(compilationService.updateCompilation(compId, dto));
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(@PathVariable long compId) {
        log.info("Получен Delete запрос к эндпоинту admin/compilations/", compId);

        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable long eventId, @PathVariable long compId) {
        log.info("Получен Patch запрос к эндпоинту admin/compilations/" + compId + " events/" + compId);
        compilationService.addEventToCompilation(eventId, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void removeEventFromCompilation(@PathVariable long compId, @PathVariable Long eventId) {
        log.info("Получен Delete запрос к эндпоинту admin/compilations/" + compId + " events/" + compId);
        compilationService.deleteEventFromCompilation(eventId, compId);
    }


}
