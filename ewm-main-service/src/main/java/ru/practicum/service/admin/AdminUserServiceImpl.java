package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadParamException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.NewUserRequest;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto postUser(NewUserRequest request) {
        User user = UserMapper.toUser(request.getName(), request.getEmail());
        repository.save(user);

        return UserMapper.toUserDto(user.getId(), user.getName(), user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(int from, int size, List<Integer> ids) {
        if (from < 0 || size < 1) {
            throw new BadParamException("Incorrect pagination params.");
        }
        List<UserDto> dtos = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            List<User> users = repository.findAllById(ids);
            for (User user : users) {
                dtos.add(UserMapper.toUserDto(user.getId(), user.getName(), user.getEmail()));
            }
            return dtos;
        }
        Slice<User> slice = repository.findAll(PageRequest.of(from, size));
        for (User user : slice) {
            dtos.add(UserMapper.toUserDto(user.getId(), user.getName(), user.getEmail()));
        }
        return dtos;
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        repository.deleteById(userId);
    }

}