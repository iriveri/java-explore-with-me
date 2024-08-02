package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.requests.RequestStatus;

import java.util.List;

@Repository
public interface ParticipationRequestRepo extends JpaRepository<ParticipationRequest, Long> {
    boolean existsByParticipantIdAndEventId(Long userId, Long eventId);

    Integer countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus confirmed);

    List<ParticipationRequest> findByParticipantId(Long userId);

    List<ParticipationRequest> findByEventId(Long eventId);


    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestStatus pending);
}
