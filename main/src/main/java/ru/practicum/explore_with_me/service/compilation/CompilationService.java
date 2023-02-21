package ru.practicum.exploreWithMe.service.compilation;

import ru.practicum.exploreWithMe.model.Compilation;
import ru.practicum.exploreWithMe.model.dto.compilation.CompilationDto;
import ru.practicum.exploreWithMe.model.dto.compilation.CompilationInDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(CompilationInDto dto);

    Compilation getCompilationById(long id);

    List<Compilation> getAllCompilation(Boolean pinned, Integer from, Integer size);

    Compilation updateCompilation(long id, CompilationInDto dto);

    void addEventToCompilation(long idEvent, long idComp);

    void deleteEventFromCompilation(long idEvent, long idComp);

    void deleteCompilation(long id);
}
