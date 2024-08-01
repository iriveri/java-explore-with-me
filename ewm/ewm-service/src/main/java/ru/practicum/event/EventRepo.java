package ru.practicum.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event,Integer> {
    void saveEvents(List<Integer> events);

    List<EventShortDto> getCompilationRepo(Long compId);
}
