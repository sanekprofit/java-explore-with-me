package ru.practicum.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByInitiator_Id(Integer userId, Pageable pageable);

    Optional<Event> findFirstByIdAndInitiator_Id(Integer eventId, Integer userId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:start IS NULL OR e.eventDate >= :start) " +
            "AND (:end IS NULL OR e.eventDate <= :end)")
    List<Event> findEventsByParameters(
            @Param("users") List<Integer> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Integer> categories,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("SELECT e FROM Event e WHERE " +
            "(LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:state IS NULL OR e.state = :state)")
    List<Event> findEventsWithDateTime(@Param("text") String text,
                           @Param("categories") List<Integer> categories,
                           @Param("paid") Boolean paid,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           @Param("state") EventState state,
                           Pageable pageable
    );

    @Query("SELECT e FROM Event e WHERE " +
            "(LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:dateTime IS NULL OR e.eventDate >= :dateTime) " +
            "AND (:state IS NULL OR e.state = :state)")
    List<Event> findEventsWithoutDateTime(@Param("text") String text,
                                          @Param("categories") List<Integer> categories,
                                          @Param("paid") Boolean paid,
                                          @Param("dateTime") LocalDateTime dateTime,
                                          @Param("state") EventState state,
                                          Pageable pageable
    );

    Optional<Event> findFirstByIdAndStateEquals(Integer eventId, EventState state);
}