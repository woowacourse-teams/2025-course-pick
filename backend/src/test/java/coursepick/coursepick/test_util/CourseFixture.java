package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;

public abstract class CourseFixture {

    public static Course.CourseBuilder course(String name, List<Coordinate> coordinates) {
        return course(name, coordinates, ADMIN_USER);
    }

    public static Course.CourseBuilder course(String name, List<Coordinate> coordinates, User user) {
        return Course.testBuilder()
                .name(new CourseName(name))
                .coordinates(coordinates)
                .simplifiedCoordinates(coordinates)
                .length(new Meter(0))
                .reviews(new ArrayList<>())
                .creatorId(user.id())
                .reportUserIds(new HashSet<>())
                .createdAt(LocalDateTime.now());
    }
}
