package ru.practicum.service;

import ru.practicum.HitResponseDto;
import ru.practicum.model.Stat;

import java.util.Set;

public interface StatService {

    Stat saveRequest(Stat stat);

    Set<HitResponseDto> getStats(String start, String end, String[] uris, Boolean unique);
}