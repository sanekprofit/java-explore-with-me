package ru.practicum.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.service.user.UserCompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class UserCompilationController {

    private final UserCompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam boolean pinned,
                                                @RequestParam(defaultValue = "0", required = false) int from,
                                                @RequestParam(defaultValue = "10", required = false) int size) {
        log.info(String.format("Received GET compilations request. pinned: {%b} from: {%d} size: {%d}", pinned, from, size));
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Integer compId) {
        log.info(String.format("Received GET compilation request. compilation id: {%d}", compId));
        return service.getCompilation(compId);
    }

}