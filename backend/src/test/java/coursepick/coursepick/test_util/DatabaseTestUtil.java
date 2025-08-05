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

    public String saveCourse(Course course) {
        return mongoTemplate.insert(course, "course").id();
    }

    public long countCourses() {
        return mongoTemplate.count(new Query(), Course.class);
    }

    public void deleteAll() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
