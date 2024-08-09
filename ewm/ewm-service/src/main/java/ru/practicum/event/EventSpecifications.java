package ru.practicum.event;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.dto.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> hasText(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), likePattern),
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Event> hasCategories(List<Long> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("category").get("id").in(categories);
        };
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, cb) -> {
            if (paid == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("paid"), paid);
        };
    }

    public static Specification<Event> hasRangeStart(LocalDateTime rangeStart) {
        return (root, query, cb) -> {
            if (rangeStart == null) {
                return cb.greaterThan(root.get("eventDate"), LocalDateTime.now());
            }
            return cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
        };
    }

    public static Specification<Event> hasRangeEnd(LocalDateTime rangeEnd) {
        return (root, query, cb) -> {
            if (rangeEnd == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
        };
    }

    public static Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(onlyAvailable)) {
                return cb.or(
                        cb.isNull(root.get("participantLimit")),
                        cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"))
                );
            }
            return cb.conjunction();
        };
    }

    public static Specification<Event> hasUsers(List<Long> users) {
        return (root, query, cb) -> {
            if (users == null || users.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("initiator").get("id").in(users);
        };
    }

    public static Specification<Event> hasStates(List<EventState> states) {
        return (root, query, cb) -> {
            if (states == null || states.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("state").in(states);
        };
    }
}