package ru.practicum;

import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class ViewStatsRepositoryTest {

    @Autowired
    private ViewStatsRepository viewStatsRepository;

    @PersistenceContext
    private static EntityManager entityManager;

    @BeforeAll
    public static void setUp() {
        // Add test data
        LocalDate date = LocalDate.of(2021, 7, 1); //01 Jul 2021
        LocalTime time = LocalTime.of(12, 0, 0);//12:00:00 GMT

        entityManager.persist(
                new ClientStatistics(1L, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date, time))); // 01 Jul 2021 12:00:00 GMT
        entityManager.persist(
                new ClientStatistics(2L, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date, time.plusMinutes(30)))); // 01 Jul 2021 12:30:00 GMT
        entityManager.persist(
                new ClientStatistics(3L, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(1), time))); // 02 Jul 2021 12:00:00 GMT
        entityManager.persist(
                new ClientStatistics(4L, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(1), time.plusMinutes(30)))); // 02 Jul 2021 12:30:00 GMT
        entityManager.persist(
                new ClientStatistics(5L, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(2), time))); // 03 Jul 2021 12:00:00 GMT
        entityManager.persist(
                new ClientStatistics(6L, "app1", "/home",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(3), time))); // 04 Jul 2021 12:00:00 GMT
        entityManager.persist(
                new ClientStatistics(7L, "app1", "/contact",
                        "127.0.0.1", LocalDateTime.of(date.plusDays(4), time.plusMinutes(30)))); // 04 Jul 2021 12:30:00 GMT
    }

    @Test
    public void testFindStatistics() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);  // 01 Jul 2021 00:00:00 GMT
        LocalDateTime end = LocalDateTime.of(2021, 7, 31, 23, 59, 59);// 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> stats = viewStatsRepository.findStatistics(start, end, uris);

        assertEquals(2, stats.size());
        for (ViewStatsDto stat : stats) {
            if (stat.getUri().equals("/home")) {
                assertEquals(4, stat.getHits());
            } else if (stat.getUri().equals("/contact")) {
                assertEquals(3, stat.getHits());
            }
        }
    }

    @Test
    public void testFindUniqueStatistics() {
        LocalDateTime start = LocalDateTime.of(2021, 7, 1, 0, 0, 0);  // 01 Jul 2021 00:00:00 GMT
        LocalDateTime end = LocalDateTime.of(2021, 7, 31, 23, 59, 59);// 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> uniqueStats = viewStatsRepository.findUniqueStatistics(start, end, uris);

        assertEquals(2, uniqueStats.size());
        for (ViewStatsDto stat : uniqueStats) {
            if (stat.getUri().equals("/home")) {
                assertEquals(4, stat.getHits());
            } else if (stat.getUri().equals("/contact")) {
                assertEquals(3, stat.getHits());
            }
        }
    }
}
