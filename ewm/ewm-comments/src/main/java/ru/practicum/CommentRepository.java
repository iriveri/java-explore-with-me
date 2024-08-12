package ru.practicum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.Compilation;

import java.util.Optional;

public interface CommentRepository  extends JpaRepository<Comment, Long> {


    Page<Comment> findByEventId(Long eventId, Pageable pageable);
}
