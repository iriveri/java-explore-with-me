package ru.practicum.request;

import lombok.Data;
import ru.practicum.dto.requests.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime created;
}
