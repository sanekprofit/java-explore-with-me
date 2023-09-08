package ru.practicum.stat;

public class StatMapper {
    public static ViewStat toViewStat(String app, String uri, long hits) {
        return new ViewStat(app, uri, hits);
    }
}
