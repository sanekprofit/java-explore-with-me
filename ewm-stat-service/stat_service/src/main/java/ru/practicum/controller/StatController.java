package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.HitResponseDto;
import ru.practicum.model.Stat;
import ru.practicum.service.StatService;

import java.util.Set;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Stat saveRequest(@RequestBody HitDto hitDto) {
        log.info("Получен запрос на сохранение запроса.");
        return statService.saveRequest(hitDto);
    }

    @GetMapping("/stats")
    public Set<HitResponseDto> getStats(@RequestParam(name = "start") String start,
                                        @RequestParam(name = "end") String end,
                                        @RequestParam(name = "uris", required = false) String[] uris,
                                        @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        log.info(String.format("Получен запрос на получение статистики. start: [%s] end: [%s] unique: %b", start, end, unique));
        return statService.getStats(start, end, uris, unique);
    }
}
