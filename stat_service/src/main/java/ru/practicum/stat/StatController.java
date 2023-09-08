package ru.practicum.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/stats")
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Stat saveRequest(@RequestBody Stat stat) {
        log.info("Получен запрос на сохранение запроса.");
        return statService.saveRequest(stat);
    }

    @GetMapping
    public Set<ViewStat> getStats(@RequestParam(name = "start") String start,
                                  @RequestParam(name = "end") String end,
                                  @RequestParam(name = "uris") String[] uris,
                                  @RequestParam(name = "unique") Boolean unique) {
        log.info(String.format("Получен запрос на получение статистики. start: [%s] end: [%s] unique: %b", start, end, unique));
        return statService.getStats(start, end, uris, unique);
    }
}
