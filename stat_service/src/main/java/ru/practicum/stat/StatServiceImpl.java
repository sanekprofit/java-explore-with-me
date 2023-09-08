package ru.practicum.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository repository;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);

    @Override
    public Stat saveRequest(Stat stat) {
        return repository.save(stat);
    }

    @Override
    public Set<ViewStat> getStats(String startStr, String endStr, String[] uris, Boolean unique) {

        Set<ViewStat> viewStats = new HashSet<>();

        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        for (String uri : uris) {
            if (!unique) {
                List<Stat> stats = repository.findAllByUriEqualsIgnoreCaseAndTimestampAfterAndTimestampBefore(uri, start, end);

                if (stats.isEmpty()) {
                    return viewStats;
                }

                for (Stat stat : stats) {
                    ViewStat viewStat = StatMapper.toViewStat(stat.getApp(), uri, stats.size());
                    viewStats.add(viewStat);
                }

            } else {
                Set<String> uniqueIps = new HashSet<>();

                List<Stat> stats = repository.findAllByUriEqualsIgnoreCaseAndTimestampAfterAndTimestampBefore(uri, start, end);

                if (stats.isEmpty()) {
                    return viewStats;
                }

                for (Stat stat : stats) {
                    uniqueIps.add(stat.getIp());

                    ViewStat viewStat = StatMapper.toViewStat(stat.getApp(), uri, uniqueIps.size());
                    viewStats.add(viewStat);
                }
            }
        }

        return viewStats;
    }
}
