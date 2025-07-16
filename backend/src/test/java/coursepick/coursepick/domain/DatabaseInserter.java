package coursepick.coursepick.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseInserter {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void saveCourse(Course course1) {
        entityManager.persist(course1);
    }

}
