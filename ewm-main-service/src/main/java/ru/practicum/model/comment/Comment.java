package ru.practicum.model.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "comment_text")
    String text;

    LocalDateTime created;

    LocalDateTime changed;

    public Comment(String text, LocalDateTime created) {
        this.text = text;
        this.created = created;
    }
}