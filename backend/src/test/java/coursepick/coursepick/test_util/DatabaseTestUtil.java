package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@TestComponent
public class DatabaseTestUtil {

    @Autowired
    MongoTemplate mongoTemplate;

    public Course saveCourse(Course course) {
        return mongoTemplate.save(course);
    }

    public void deleteCourses() {
        mongoTemplate.remove(new Query(), Course.class);
    }

    public long countCourses() {
        return mongoTemplate.count(new Query(), Course.class);
    }
}
