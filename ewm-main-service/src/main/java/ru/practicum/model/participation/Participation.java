package ru.practicum.model.participation;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.event.Event;
import ru.practicum.model.participation.enums.ParticipantState;
import ru.practicum.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participations")
@NoArgsConstructor
public class Participation {

    LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    ParticipantState status = ParticipantState.PENDING;

    public Participation(Event event, User requester) {
        this.event = event;
        this.requester = requester;
    }
}