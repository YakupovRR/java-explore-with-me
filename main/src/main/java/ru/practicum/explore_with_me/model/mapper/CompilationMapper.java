package ru.practicum.explore_with_me.model.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.model.Compilation;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationInDto;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDto toDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(compilation.getEvents().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toSet()));
        return compilationDto;
    }

    public Compilation toCompilation(CompilationInDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        return compilation;
    }

}
