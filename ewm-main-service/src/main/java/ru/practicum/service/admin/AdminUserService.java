package ru.practicum.service.admin;

import ru.practicum.model.user.dto.NewUserRequest;
import ru.practicum.model.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {

    UserDto postUser(NewUserRequest request);

    List<UserDto> getUsers(int from, int size, List<Integer> ids);

    void deleteUser(Integer userId);

}