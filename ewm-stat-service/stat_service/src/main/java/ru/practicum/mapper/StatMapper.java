package ru.practicum.mapper;

import ru.practicum.HitResponseDto;

public class StatMapper {
    public static HitResponseDto toViewStat(String app, String uri, long hits) {
        return new HitResponseDto(app, uri, hits);
    }
}