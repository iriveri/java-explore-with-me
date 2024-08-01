package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.statistics.ViewStatsDto;
import ru.practicum.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT new ru.practicum.user.UserDto(u.id, u.email, u.name) " +
            "FROM users u " +
            "WHERE u.id IN (ids)")
    Slice<UserDto> getAllUsers(@Param("ids") List<Long> ids, Pageable pageable);

}
