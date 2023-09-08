package ru.practicum.stat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    List<Stat> findAllByUriEqualsIgnoreCaseAndTimestampAfterAndTimestampBefore(String uri, LocalDateTime start, LocalDateTime end);

}
