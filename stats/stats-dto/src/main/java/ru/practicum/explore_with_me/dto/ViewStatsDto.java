package ru.practicum.explore_with_me.dto;

import lombok.Data;

@Data
public class ViewStatsDto {
    private Long hits;
    private String app;
    private String uri;
}
