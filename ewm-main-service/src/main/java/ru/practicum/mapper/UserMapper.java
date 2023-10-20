package ru.practicum.mapper;

import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.model.user.dto.UserShortDto;

public class UserMapper {
    public static User toUser(String name, String email) {
        return new User(name, email);
    }

    public static UserDto toUserDto(int id, String name, String email) {
        return new UserDto(id, name, email);
    }

    public static UserShortDto toUserShortDto(int id, String name) {
        return new UserShortDto(id, name);
    }

}
