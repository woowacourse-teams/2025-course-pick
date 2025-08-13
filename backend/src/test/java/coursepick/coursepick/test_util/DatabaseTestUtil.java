package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DatabaseTestUtil {

    @Autowired
    MongoTemplate mongoTemplate;

    @Transactional
    public Course saveCourse(Course course) {
        return mongoTemplate.save(course);
    }

    @Transactional
    public void deleteCourses() {
        mongoTemplate.remove(new Query(), Course.class);
    }

    @Transactional(readOnly = true)
    public long countCourses() {
        return mongoTemplate.count(new Query(), Course.class);
    }
}
