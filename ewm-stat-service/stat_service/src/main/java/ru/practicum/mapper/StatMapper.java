package ru.practicum.mapper;

import ru.practicum.HitResponseDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;

public class StatMapper {
    public static HitResponseDto toViewStat(String app, String uri, long hits) {
        return new HitResponseDto(app, uri, hits);
    }

    public static Stat toStat(String app, String uri, String ip, LocalDateTime timestamp) {
        return new Stat(app, uri, ip, timestamp);
    }
}