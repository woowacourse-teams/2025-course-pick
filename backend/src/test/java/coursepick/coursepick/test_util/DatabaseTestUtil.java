package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.UserCreatedCourse;
import coursepick.coursepick.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;

@TestComponent
public class DatabaseTestUtil {

    @Autowired
    MongoTemplate mongoTemplate;

    public Course saveCourse(Course course) {
        return mongoTemplate.insert(course, "course");
    }

    public void saveAllCourses(Collection<Course> courses) {
        mongoTemplate.insertAll(courses);
    }

    public void deleteCourses() {
        mongoTemplate.remove(new Query(), Course.class);
    }

    public long countCourses() {
        return mongoTemplate.count(new Query(), Course.class);
    }

    public Course findCourseById(String id) {
        return mongoTemplate.findById(id, Course.class);
    }

    public User saveUser(User user) {
        return mongoTemplate.insert(user, "user");
    }

    public UserCreatedCourse findUserCourse(String userId, String courseId) {
        return mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId)
                        .and("courseId").is(courseId)),
                UserCreatedCourse.class
        );
    }

    public void deleteUsers() {
        mongoTemplate.remove(new Query(), User.class);
    }

    public void deleteUserCreatedCourses() {
        mongoTemplate.remove(new Query(), UserCreatedCourse.class);
    }
}
