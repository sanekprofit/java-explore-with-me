package ru.practicum.stat;

import java.util.Set;

public interface StatService {

    Stat saveRequest(Stat stat);

    Set<ViewStat> getStats(String start, String end, String[] uris, Boolean unique);
}
