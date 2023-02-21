package ru.practicum.exploreWithMe.service;

import ru.practicum.exploreWithMe.model.Hit;
import ru.practicum.exploreWithMe.model.ViewStats;

import java.util.List;

public interface StatsService {
    Hit addHit(Hit hit);

    List<ViewStats> getViewStats(String start, String end, String[] uris, Boolean unique);

}
