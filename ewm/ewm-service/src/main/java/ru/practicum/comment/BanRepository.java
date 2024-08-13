package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BanRepository extends JpaRepository<CommentBan, Long> {

    Optional<Object> findByEventIdAndUserId(Long eventId, Long userId);
}
