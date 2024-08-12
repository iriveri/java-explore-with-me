package ru.practicum.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.CommentBan;
import ru.practicum.compilation.Compilation;

import java.util.Optional;

public interface BanRepository  extends JpaRepository<CommentBan, Long> {
    boolean findByEventAndUser();

    Optional<Object> findByEventIdAndUserId(Long eventId, Long userId);
}
