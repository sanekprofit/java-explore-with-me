package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stats")
public class DtoController {
    private final DtoClient dtoClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveRequest(@RequestBody Dto dto) {
        return dtoClient.saveRequest(dto);
    }

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris", required = false) String[] uris,
                                           @RequestParam(name = "unique", required = false) Boolean unique) {
        return dtoClient.getStats(start, end, uris, unique);
    }
}
