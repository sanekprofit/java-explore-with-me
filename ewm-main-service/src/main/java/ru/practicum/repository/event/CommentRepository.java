package ru.practicum.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEvent_Id(Long eventId);

}