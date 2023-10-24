package ru.practicum.mapper;

import ru.practicum.model.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.participation.dto.ParticipationRequestDto;
import ru.practicum.model.participation.enums.ParticipantState;

import java.time.LocalDateTime;
import java.util.List;

public class ParticipationMapper {

    public static ParticipationRequestDto toDto(LocalDateTime created, Long eventId, int id, int requester, ParticipantState status) {
        return new ParticipationRequestDto(created, eventId, id, requester, status);
    }

    public static EventRequestStatusUpdateResult toResult(List<ParticipationRequestDto> confirmed, List<ParticipationRequestDto> canceled) {
        return new EventRequestStatusUpdateResult(confirmed, canceled);
    }
}