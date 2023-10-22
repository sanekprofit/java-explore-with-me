package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.user.dto.NewUserRequest;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.service.admin.AdminUserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Valid @RequestBody NewUserRequest request) {
        log.info(String.format("Received POST user request. request: {%s}", request));
        return service.postUser(request);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "0", required = false) int from,
                                  @RequestParam(defaultValue = "10", required = false) int size,
                                  @RequestParam(required = false) List<Integer> ids) {
        log.info(String.format("Received GET users request. from: {%d} size: {%d} ids: {%s}", from, size, ids));
        return service.getUsers(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        log.info(String.format("Received DELETE user request. userId: {%d}", userId));
        service.deleteUser(userId);
    }

}