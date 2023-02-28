package ru.practicum.explore_with_me.service;

import ru.practicum.explore_with_me.model.Hit;
import ru.practicum.explore_with_me.model.ViewStats;

import java.util.List;

public interface StatsService {
    Hit addHit(Hit hit);

    List<ViewStats> getViewStats(String start, String end, String[] uris, Boolean unique);

}
