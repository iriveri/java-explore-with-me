package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
public class ViewStatsRepositoryTest {

    @Autowired
    private ViewStatsRepository viewStatsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {

        LocalDate date = LocalDate.of(2021, 7, 1); //01 Jul 2021
        LocalTime time = LocalTime.of(12, 0, 0);//12:00:00 GMT

        entityManager.persist( // 01 Jul 2021 12:00:00 GMT
                new ClientStatistics(null, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date, time)));
        entityManager.persist(// 01 Jul 2021 12:30:00 GMT
                new ClientStatistics(null, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date, time.plusMinutes(30))));
        entityManager.persist( // 02 Jul 2021 12:00:00 GMT
                new ClientStatistics(null, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(1), time)));
        entityManager.persist(// 02 Jul 2021 12:30:00 GMT
                new ClientStatistics(null, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(1), time.plusMinutes(30))));
        entityManager.persist( // 03 Jul 2021 12:00:00 GMT
                new ClientStatistics(null, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(2), time)));
        entityManager.persist( // 04 Jul 2021 12:00:00 GMT
                new ClientStatistics(null, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(3), time)));
        entityManager.persist( // 04 Jul 2021 12:30:00 GMT
                new ClientStatistics(null, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(4), time.plusMinutes(30))));
    }

    @Test
    public void testFindStatistics() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);  // 01 Jul 2021 00:00:00 GMT
        LocalDateTime end = LocalDateTime.of(2021, 7, 31, 23, 59, 59);// 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> stats = viewStatsRepository.findStatistics(start, end, uris);

        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        // Assuming expected data:
        Map<String, Long> expectedHits = Map.of("/home", 4L, "/contact", 3L);

        for (ViewStatsDto stat : stats) {
            assertTrue(expectedHits.containsKey(stat.getUri()), "Unexpected URI: " + stat.getUri());
            assertEquals(expectedHits.get(stat.getUri()), stat.getHits(), "Unexpected hits for URI: " + stat.getUri());
        }
    }

    @Test
    public void testFindUniqueStatistics() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 7, 31, 23, 59, 59);
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> uniqueStats = viewStatsRepository.findUniqueStatistics(start, end, uris);

        assertNotNull(uniqueStats);
        assertFalse(uniqueStats.isEmpty());

        // Assuming expected data:
        Map<String, Long> expectedHits = Map.of("/home", 1L, "/contact", 1L);

        for (ViewStatsDto stat : uniqueStats) {
            assertTrue(expectedHits.containsKey(stat.getUri()), "Unexpected URI: " + stat.getUri());
            assertEquals(expectedHits.get(stat.getUri()), stat.getHits(), "Unexpected hits for URI: " + stat.getUri());
        }
    }
    @Test
    public void testEmptyUriList() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);  // 01 Jul 2021 00:00:00 GMT
        LocalDateTime end = LocalDateTime.of(2021, 7, 31, 23, 59, 59);// 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("");

        List<ViewStatsDto> stats = viewStatsRepository.findStatistics(start, end, uris);

        assertEquals(0, stats.size());
    }

    @Test
    public void testDistinctTimeLimits() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);  // 01 Jul 2021 00:00:00 GMT
        LocalDateTime end = LocalDateTime.of(2021, 7, 2, 23, 59, 59);// 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> stats = viewStatsRepository.findStatistics(start, end, uris);

        assertEquals(4, stats.size());
    }
}
