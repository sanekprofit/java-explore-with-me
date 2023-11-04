package ru.practicum.mapper;

import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.model.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(String text, LocalDateTime created) {
        return new Comment(text, created);
    }

    public static CommentDto toDto(Long id, UserShortDto userShortDto, String text, LocalDateTime created, LocalDateTime changed) {
        return new CommentDto(id, userShortDto, text, created, changed);
    }
}