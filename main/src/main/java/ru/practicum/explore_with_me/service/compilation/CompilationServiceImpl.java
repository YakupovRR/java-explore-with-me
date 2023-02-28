package ru.practicum.explore_with_me.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.model.Compilation;
import ru.practicum.explore_with_me.model.event.Event;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationInDto;
import ru.practicum.explore_with_me.model.mapper.CompilationMapper;
import ru.practicum.explore_with_me.repository.CompilationRepository;
import ru.practicum.explore_with_me.repository.EventRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(CompilationInDto dto) {
        Compilation compilation = compilationMapper.toCompilation(dto);
        Set<Event> events = eventRepository.getEventsById(dto.getEvents());
        compilation.setEvents(events);
        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public Compilation getCompilationById(long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Compilation> getAllCompilation(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return compilationRepository.getAll(pinned, pageable).getContent();
    }

    @Override
    @Transactional
    public Compilation updateCompilation(long id, CompilationInDto dto) {
        Compilation compilation =
                compilationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            Set<Event> e = new HashSet<>(events);
            compilation.setEvents(e);
        }
        return compilation;
    }

    @Override
    @Transactional
    public void addEventToCompilation(long idEvent, long idComp) {
        Compilation compilation = compilationRepository.findById(idComp)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));
        compilation.getEvents().add(event);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(long idEvent, long idComp) {
        Compilation compilation = compilationRepository.findById(idComp)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND));

        compilation.getEvents().removeIf((e) -> Objects.equals(e.getId(), idEvent));
    }

    @Override
    @Transactional
    public void deleteCompilation(long id) {
        compilationRepository.deleteById(id);
    }
}
