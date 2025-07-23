package coursepick.coursepick.test_util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void deleteCourses() {
        entityManager.createQuery("DELETE FROM Course");
    }

}
