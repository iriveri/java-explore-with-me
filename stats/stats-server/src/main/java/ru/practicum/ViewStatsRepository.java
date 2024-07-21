package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViewStatsRepository extends JpaRepository<ClientStatistics, Long> {

    @Query("SELECT new ru.practicum.ViewStatsDto(v.app, v.uri, COUNT(v)) " +
            "FROM ClientStatistics v " +
            "WHERE v.timestamp BETWEEN :start AND :end " +
            "AND  v.uri IN :uris " +
            "GROUP BY v.uri")
    List<ViewStatsDto> findStatistics(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(v.app, v.uri, COUNT(DISTINCT v.ip)) " +
            "FROM ClientStatistics v " +
            "WHERE v.timestamp BETWEEN :start AND :end " +
            "AND  v.uri IN :uris " +
            "GROUP BY v.uri")
    List<ViewStatsDto> findUniqueStatistics(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);
}