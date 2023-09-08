package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatController {
    private final StatClient statClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveRequest(@RequestBody StatDto statDto) {
        log.info(String.format("Получен запрос на сохранение запроса: %s", statDto));
        return statClient.saveRequest(statDto);
    }

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris", required = false) String[] uris,
                                           @RequestParam(name = "unique", required = false) Boolean unique) {
        log.info(String.format("Получен запрос на получение статистики. start: [%s] end: [%s] unique: %b", start, end, unique));
        return statClient.getStats(start, end, uris, unique);
    }
}
