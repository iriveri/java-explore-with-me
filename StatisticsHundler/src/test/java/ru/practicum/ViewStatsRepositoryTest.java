package ru.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
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
        entityManager.persist(new ClientStatistics(1L, "app1", "/home","127.0.0.1", new Date(1625097600000L))); // 01 Jul 2021 12:00:00 GMT
        entityManager.persist(new ClientStatistics(2L, "app1", "/contact","127.0.0.1", new Date(1625101200000L))); // 01 Jul 2021 12:30:00 GMT
        entityManager.persist(new ClientStatistics(3L, "app1", "/home","127.0.0.1", new Date(1625184000000L))); // 02 Jul 2021 12:00:00 GMT
        entityManager.persist(new ClientStatistics(4L, "app1", "/contact","127.0.0.1", new Date(1625187600000L))); // 02 Jul 2021 12:30:00 GMT
        entityManager.persist(new ClientStatistics(5L, "app1", "/home","127.0.0.1", new Date(1625270400000L))); // 03 Jul 2021 12:00:00 GMT
        entityManager.persist(new ClientStatistics(6L, "app1", "/home","127.0.0.1", new Date(1625356800000L))); // 04 Jul 2021 12:00:00 GMT
        entityManager.persist(new ClientStatistics(7L, "app1", "/contact","127.0.0.1", new Date(1625360400000L))); // 04 Jul 2021 12:30:00 GMT
    }

    @Test
    public void testFindStatistics() {
        Date start = new Date(1625097600000L); // 01 Jul 2021 00:00:00 GMT
        Date end = new Date(1627689600000L);   // 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> stats = viewStatsRepository.findStatistics(start, end, uris);

        assertEquals(2, stats.size());
        // Additional assertions based on your test data
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
        Date start = new Date(1625097600000L); // 01 Jul 2021 00:00:00 GMT
        Date end = new Date(1627689600000L);   // 31 Jul 2021 23:59:59 GMT
        List<String> uris = Arrays.asList("/home", "/contact");

        List<ViewStatsDto> uniqueStats = viewStatsRepository.findUniqueStatistics(start, end, uris);

        assertEquals(2, uniqueStats.size());
        // Additional assertions based on your test data
        for (ViewStatsDto stat : uniqueStats) {
            if (stat.getUri().equals("/home")) {
                assertEquals(4, stat.getHits());
            } else if (stat.getUri().equals("/contact")) {
                assertEquals(3, stat.getHits());
            }
        }
    }
}
