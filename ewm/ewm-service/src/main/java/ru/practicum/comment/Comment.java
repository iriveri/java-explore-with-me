package ru.practicum.comment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table(name = "compilation")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime editedAt;

    @Column
    private Boolean pinned;
}

