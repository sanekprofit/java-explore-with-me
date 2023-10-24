package ru.practicum.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.participation.Participation;
import ru.practicum.model.participation.enums.ParticipantState;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

    Optional<Participation> findFirstByRequester_Id(Integer requester);

    List<Participation> findAllByEvent_IdAndEvent_Initiator_Id(Long eventId, Integer userId);

    List<Participation> findAllByStatusEqualsAndEvent_Id(ParticipantState status, Long eventId);

    List<Participation> findAllByRequester_Id(Integer userId);

    Optional<Participation> findFirstByRequester_IdAndId(Integer userId, Integer requestId);

}