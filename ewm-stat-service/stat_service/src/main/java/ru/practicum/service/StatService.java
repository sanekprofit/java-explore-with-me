package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.HitResponseDto;
import ru.practicum.model.Stat;

import java.util.Set;

public interface StatService {

    Stat saveRequest(HitDto hitDto);

    Set<HitResponseDto> getStats(String start, String end, String[] uris, Boolean unique);
}