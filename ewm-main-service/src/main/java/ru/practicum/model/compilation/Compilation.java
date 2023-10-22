package ru.practicum.model.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Event;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {

    @ManyToMany
    @JoinTable(
            name = "compilation_event",
            joinColumns = { @JoinColumn(name = "compilation_id") },
            inverseJoinColumns = { @JoinColumn(name = "event_id") }
    )
    List<Event> events;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    boolean pinned;

    String title;

    public Compilation(List<Event> events, boolean pinned, String title) {
        this.events = events;
        this.pinned = pinned;
        this.title = title;
    }
}