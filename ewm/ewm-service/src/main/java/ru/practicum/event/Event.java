package ru.practicum.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
    Long category_id;
    Long confirmed_requests;
    LocalDateTime created_on;
    String description;
    LocalDateTime event_date;
    Long initiator_id;
    Long location_id;
    Boolean paid;
    Integer participant_limit;
    LocalDateTime published_on;
    Boolean request_moderation;
    EventState state;
    String title;
    Long views;

}
