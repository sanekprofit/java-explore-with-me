package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.HitResponseDto;
import ru.practicum.model.Stat;

import java.util.List;

public interface StatService {

    Stat saveRequest(HitDto hitDto);

    List<HitResponseDto> getStats(String start, String end, List<String> uris, Boolean unique);
}