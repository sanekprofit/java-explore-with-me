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

    Optional<Participation> findFirstByEvent_Initiator_Id(Integer initiator);

    List<Participation> findAllByStatusEquals(ParticipantState participantState);

}