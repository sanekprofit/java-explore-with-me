package ru.practicum.repository.event;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.enums.EventState;
import ru.practicum.model.utilities.SelfFormatter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventStorage {

    private final SessionFactory sessionFactory;

    public List<Event> getAdminEventsSearch(List<Integer> users, List<String> states, List<Integer> categories, String start, String end, int from, int size) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = cb.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new LinkedList<>();

        if (users != null) {
            predicates.add(root.get("initiator").get("id").in(users));
        }

        if (states != null) {
            predicates.add(root.get("state").as(EventState.class).in(parseStates(states)));
        }

        if (categories != null) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), parseDateTime(start)));
        }

        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), parseDateTime(end)));
        }

        criteriaQuery.select(root).where(cb.and(predicates.toArray(Predicate[]::new)));

        Query<Event> query = session.createQuery(criteriaQuery);

        query.setFirstResult(from);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public List<Event> getPublicEvents(String text,
                                       List<Integer> categories,
                                       Boolean paid,
                                       String rangeStart,
                                       String rangeEnd,
                                       int from,
                                       int size) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = cb.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new LinkedList<>();

        if (text != null && !text.isBlank() && !text.equals("0")) {
            predicates.add(cb.or(
                    createSearchPredicate(cb, root, "title", text),
                    createSearchPredicate(cb, root, "annotation", text),
                    createSearchPredicate(cb, root, "description", text)));
        }

        if (categories != null && categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), parseDateTime(rangeStart)));
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), parseDateTime(rangeEnd)));
        } else {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        criteriaQuery.select(root).where(cb.and(predicates.toArray(Predicate[]::new)));

        Query<Event> query = session.createQuery(criteriaQuery);

        query.setFirstResult(from);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private Predicate createSearchPredicate(CriteriaBuilder builder,
                                           Root<Event> root,
                                           String field,
                                           String searchSrt) {
        return builder.like(builder.lower(root.get(field).as(String.class)),
                "%" + searchSrt.toLowerCase() + "%");
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return dateTime == null ? null : LocalDateTime.parse(dateTime, SelfFormatter.FORMAT);
    }

    private List<EventState> parseStates(List<String> states) {
        if (states != null && !states.isEmpty()) {
            return states.stream()
                    .map(EventState::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }
}