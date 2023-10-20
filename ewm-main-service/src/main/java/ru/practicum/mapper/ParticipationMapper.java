package ru.practicum.mapper;

import ru.practicum.model.participation.dto.ParticipationRequestDto;
import ru.practicum.model.participation.enums.ParticipantState;

import java.time.LocalDateTime;

public class ParticipationMapper {
    public static ParticipationRequestDto toDto(LocalDateTime created, int eventId, int id, int requester, ParticipantState status) {
        return new ParticipationRequestDto(created, eventId, id, requester, status);
    }
}