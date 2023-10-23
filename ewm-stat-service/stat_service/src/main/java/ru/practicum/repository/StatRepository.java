package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    List<Stat> findAllByUriEqualsAndTimestampAfterAndTimestampBefore(String uri, LocalDateTime start, LocalDateTime end);

    List<Stat> findAllByTimestampAfterAndTimestampBefore(LocalDateTime start, LocalDateTime end);
}