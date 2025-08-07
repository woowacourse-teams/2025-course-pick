package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DatabaseTestUtil {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveCourse(Course course) {
        entityManager.persist(course);
    }

    @Transactional
    public void deleteCourses() {
        entityManager.createQuery("DELETE FROM Course").executeUpdate();
    }

    @Transactional(readOnly = true)
    public long countCourses() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course", Long.class);
    }
}
