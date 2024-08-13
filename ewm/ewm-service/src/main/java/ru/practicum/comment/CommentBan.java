package ru.practicum.comment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.Event;
import ru.practicum.user.User;

@Data
@NoArgsConstructor
@Table(name = "comment_ban")
@Entity
public class CommentBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
