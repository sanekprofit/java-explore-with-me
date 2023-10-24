package ru.practicum.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.enums.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiator_Id(Integer userId, Pageable pageable);

    Optional<Event> findFirstByIdAndInitiator_Id(Long eventId, Integer userId);

    Optional<Event> findFirstByIdAndStateEquals(Long eventId, EventState state);
}