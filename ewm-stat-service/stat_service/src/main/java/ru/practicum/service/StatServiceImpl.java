package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.HitResponseDto;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository repository;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);

    @Override
    public Stat saveRequest(HitDto hitDto) {
        Stat hit = StatMapper.toStat(hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        return repository.save(hit);
    }

    @Override
    public List<HitResponseDto> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {

        Set<HitResponseDto> viewStats = new HashSet<>();

        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        if (start.isAfter(end)) {
            throw new BadParamException("Start is after end");
        }

        if (uris == null || uris.isEmpty() || uris.get(0).equals("/events")) {
            if (!unique) {
                List<Stat> stats = repository.findAllByTimestampBetween(start, end);

                if (stats.isEmpty()) {
                    return new ArrayList<>(viewStats);
                }

                Map<String, Integer> uriViewCounts = new HashMap<>();

                for (Stat stat : stats) {
                    String uri = stat.getUri();
                    int viewCount = uriViewCounts.getOrDefault(uri, 0) + 1;
                    uriViewCounts.put(uri, viewCount);
                }

                int maxViewCount = Collections.max(uriViewCounts.values());

                for (Map.Entry<String, Integer> entry : uriViewCounts.entrySet()) {
                    if (entry.getValue() == maxViewCount) {
                        HitResponseDto viewStat = StatMapper.toViewStat("ewm-main-service", entry.getKey(), maxViewCount);
                        viewStats.add(viewStat);
                    }
                }
            } else {
                Set<String> uniqueIps = new HashSet<>();

                List<Stat> stats = repository.findAllByTimestampBetween(start, end);

                if (stats.isEmpty()) {
                    return new ArrayList<>(viewStats);
                }

                for (Stat stat : stats) {
                    uniqueIps.add(stat.getIp());

                    HitResponseDto viewStat = StatMapper.toViewStat(stat.getApp(), stat.getUri(), uniqueIps.size());
                    viewStats.add(viewStat);
                }
            }
        } else {
            for (String uri : uris) {
                if (!unique) {
                    List<Stat> stats = repository.findAllByUriEqualsAndTimestampBetween(uri, start, end);

                    if (stats.isEmpty()) {
                        return new ArrayList<>(viewStats);
                    }

                    Map<String, Integer> uriViewCounts = new HashMap<>();

                    for (Stat stat : stats) {
                        String uri1 = stat.getUri();
                        int viewCount = uriViewCounts.getOrDefault(uri1, 0) + 1;
                        uriViewCounts.put(uri1, viewCount);
                    }

                    int maxViewCount = Collections.max(uriViewCounts.values());

                    for (Map.Entry<String, Integer> entry : uriViewCounts.entrySet()) {
                        if (entry.getValue() == maxViewCount) {
                            HitResponseDto viewStat = StatMapper.toViewStat("ewm-main-service", entry.getKey(), maxViewCount);
                            viewStats.add(viewStat);
                        }
                    }
                } else {
                    Set<String> uniqueIps = new HashSet<>();

                    List<Stat> stats = repository.findAllByUriEqualsAndTimestampBetween(uri, start, end);

                    if (stats.isEmpty()) {
                        return new ArrayList<>(viewStats);
                    }

                    for (Stat stat : stats) {
                        uniqueIps.add(stat.getIp());

                        HitResponseDto viewStat = StatMapper.toViewStat(stat.getApp(), uri, uniqueIps.size());
                        viewStats.add(viewStat);
                    }
                }
            }
        }
        List<HitResponseDto> viewList = new ArrayList<>(viewStats);
        viewList.sort(Comparator.comparing(HitResponseDto::getHits).reversed());
        return viewList;
    }
}
